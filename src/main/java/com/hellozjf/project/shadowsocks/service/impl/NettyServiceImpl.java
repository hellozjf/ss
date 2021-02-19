package com.hellozjf.project.shadowsocks.service.impl;

import com.hellozjf.project.shadowsocks.dao.entity.User;
import com.hellozjf.project.shadowsocks.handler.CipherDecoder;
import com.hellozjf.project.shadowsocks.handler.CipherEncoder;
import com.hellozjf.project.shadowsocks.handler.ShadowsocksDecoder;
import com.hellozjf.project.shadowsocks.handler.TargetHandler;
import com.hellozjf.project.shadowsocks.service.CryptService;
import com.hellozjf.project.shadowsocks.service.NettyService;
import com.hellozjf.project.shadowsocks.service.UserService;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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
                    Channel channel = createPort(user.getPort(), user.getPassword(), "aes-256-gcm");
                    portChannelMap.put(user.getPort(), channel);
                } catch (Exception e) {
                    log.error("e = {}", e.getMessage());
                }
            }
        }
    }

    @Override
    public Channel createPort(int port, String password, String method) throws InterruptedException {
        byte[] key = cryptService.getKey(password);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        long threadId = Thread.currentThread().getId();
                        log.debug("threadId:{} 接入客户端，ch = {}", threadId, ch);
                        ch.pipeline()
                                .addLast(new CipherEncoder(threadId, key))
                                .addLast(new CipherDecoder(threadId, key))
                                .addLast(new ShadowsocksDecoder(threadId));
                    }
                });
        // 启动端口
        log.info("正在启动ss端口，port[{}]，password[{}], method[{}]", port, password, method);
        ChannelFuture channelFuture = serverBootstrap.bind().sync();
        return channelFuture.channel();
    }

    @Override
    public Channel connectTarget(String address, int port, Channel clientHandler, long threadId) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(address, port)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        log.debug("threadId:{} 连接目标 address:{}, port:{}, ", threadId, address, port);
                        ch.pipeline()
                                .addLast(new TargetHandler(clientHandler, threadId));
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect().sync();
        return channelFuture.channel();
    }
}
