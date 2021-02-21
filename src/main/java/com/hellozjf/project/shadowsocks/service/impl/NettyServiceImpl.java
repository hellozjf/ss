package com.hellozjf.project.shadowsocks.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.hellozjf.project.shadowsocks.config.SnowflakeConfig;
import com.hellozjf.project.shadowsocks.dao.entity.User;
import com.hellozjf.project.shadowsocks.handler.*;
import com.hellozjf.project.shadowsocks.service.CryptService;
import com.hellozjf.project.shadowsocks.service.NettyService;
import com.hellozjf.project.shadowsocks.service.UserService;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@Service
public class NettyServiceImpl implements NettyService {

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("md5MessageDigest")
    private MessageDigest md5MessageDigest;

    @Autowired
    private CryptService cryptService;

    @Autowired
    @Qualifier("bossGroup")
    private EventLoopGroup bossGroup;

    @Autowired
    @Qualifier("workerGroup")
    private EventLoopGroup workerGroup;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    private Map<Integer, Channel> portChannelMap = new ConcurrentHashMap<>();

    @Override
    public void init() {
        // 查出所有用户信息
        List<User> userList = userService.list();
        // 根据用户信息启动相应的端口
        for (User user : userList) {
            if (user.getPort() != null && StringUtils.hasLength(user.getPassword())) {
                try {
                    // 启动端口，加密方式写死为aes-256-gcm
                    createPort(user.getPort(), user.getPassword(), "aes-256-gcm");
                } catch (Exception e) {
                    log.error("e = {}", e.getMessage());
                }
            }
        }
    }

    @Override
    public Channel createPort(int port, String password, String method) throws InterruptedException {

        // 根据密码生成相应的key
        byte[] key = cryptService.getKey(password);

        // 创建一个流量整形Handler
        // todo 速率需要从数据库中获取
        GlobalTrafficShapingHandler globalTrafficShapingHandler = new GlobalTrafficShapingHandler(
                scheduledExecutorService,
                10 * 1024,
                10 * 1024,
                1000,
                Integer.MAX_VALUE
        );

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                // TCP传输不要延迟
                .childOption(ChannelOption.TCP_NODELAY, true)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        long threadId = snowflake.nextId();
                        log.debug("threadId:{} 接入客户端，ch = {}", threadId, ch);
                        ch.pipeline()
                                .addLast(new FlowOutRecordHandler(port, threadId))
                                .addLast(new FlowInRecordHandler(port, threadId))
                                .addLast(globalTrafficShapingHandler)
                                .addLast(new CipherEncoder(threadId, key))
                                .addLast(new CipherDecoder(threadId, key))
                                .addLast(new ShadowsocksDecoder(threadId));
                    }
                });
        // 启动端口
        log.info("正在启动ss端口，port[{}]，password[{}], method[{}]", port, password, method);
        ChannelFuture channelFuture = serverBootstrap.bind().sync();
        Channel channel = channelFuture.channel();
        portChannelMap.put(port, channel);
        channel.closeFuture();
        return channel;
    }

    @Override
    public void deletePort(int port) {
        Channel channel = portChannelMap.get(port);
        channel.close();
        portChannelMap.remove(port);
    }

    @Override
    public void connectTarget(String address, int port, Channel clientChannel, ClientInHandler clientInHandler, long threadId) throws InterruptedException {

        // 配置连接
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                // TCP传输不要延迟
                .option(ChannelOption.TCP_NODELAY, true)
                // 连接超时时间设置为10秒
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000)
                .remoteAddress(address, port)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        log.debug("threadId:{} 连接目标 address:{} port:{}, ", threadId, address, port);
                        ch.pipeline()
                                .addLast(new TargetInHandler(clientChannel, threadId));
                    }
                });

        // 连接，并判断连接是成功还是失败
        ChannelFuture channelFuture = null;
        channelFuture = bootstrap.connect();
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("threadId:{} 连接目标成功 address:{} port:{}", threadId, address, port);
                Channel channel = future.channel();
                channel.closeFuture();
                clientInHandler.setTargetChannel(channel);
            } else {
                log.error("threadId:{} 连接目标失败 address:{} port:{} cause:{}", threadId, address, port, future.cause().getMessage());
                if (future.channel().isActive()) {
                    future.channel().close();
                }
                if (clientChannel.isActive()) {
                    clientChannel.close();
                }
            }
        });
    }
}
