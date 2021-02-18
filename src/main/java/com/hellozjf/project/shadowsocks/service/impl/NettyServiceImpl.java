package com.hellozjf.project.shadowsocks.service.impl;

import com.hellozjf.project.shadowsocks.dao.entity.User;
import com.hellozjf.project.shadowsocks.handler.CipherDecoder;
import com.hellozjf.project.shadowsocks.handler.CipherEncoder;
import com.hellozjf.project.shadowsocks.handler.ShadowsocksDecoder;
import com.hellozjf.project.shadowsocks.handler.TargetHandler;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class NettyServiceImpl implements NettyService {

    @Autowired
    private UserService userService;

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
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(16);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        log.debug("接入客户端，ch = {}", ch);
                        ch.pipeline()
                                .addLast(new CipherEncoder(password))
                                .addLast(new CipherDecoder(password))
                                .addLast(new ShadowsocksDecoder(NettyServiceImpl.this));
                    }
                });
        // 启动端口
        log.info("正在启动ss端口，port[{}]，password[{}], method[{}]", port, password, method);
        ChannelFuture channelFuture = serverBootstrap.bind().sync();
        return channelFuture.channel();
    }

    @Override
    public Channel connectTarget(String address, int port, Channel clientHandler) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(address, port)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        log.debug("连接目标，address:{}, port:{}", address, port);
                        ch.pipeline()
                                .addLast(new TargetHandler(clientHandler));
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect().sync();
        return channelFuture.channel();
    }
}
