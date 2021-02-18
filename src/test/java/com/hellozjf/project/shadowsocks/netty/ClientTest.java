package com.hellozjf.project.shadowsocks.netty;

import com.hellozjf.project.shadowsocks.BaseTest;
import com.hellozjf.project.shadowsocks.handler.EncryptionDecoder;
import com.hellozjf.project.shadowsocks.handler.EncryptionEncoder;
import com.hellozjf.project.shadowsocks.handler.ServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ClientTest extends BaseTest {

    @Test
    public void connectTest() throws InterruptedException {

        int port = 8388;
        String password = "123456";
        String method = "aes-256-gcm";

        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress("localhost", port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new EncryptionEncoder(password))
                                    .addLast(new EncryptionDecoder(password))
                                    .addLast(new ServerHandler());
                        }
                    });
            // 连上我开启的服务端
            ChannelFuture channelFuture = bootstrap.connect().sync();
            Channel serverChannel = channelFuture.channel();
            new ClientThread(serverChannel).start();
            serverChannel.closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    /**
     * 客户端线程
     */
    public static class ClientThread extends Thread {

        private Channel channel;

        public ClientThread(Channel serverChannel) {
            this.channel = serverChannel;
        }

        @Override
        public void run() {

            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            byte type = 0x03;
            String address = "www.baidu.com";
            short port = 80;
            String content = "GET / HTTP/1.0\\r\\nHost: www.baidu.com.com\\r\\n\\r\\n";

            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeByte(type);
            byteBuf.writeByte(address.length());
            byteBuf.writeBytes(address.getBytes(CharsetUtil.UTF_8));
            byteBuf.writeShort(port);
            byteBuf.writeBytes(content.getBytes(CharsetUtil.UTF_8));
            channel.writeAndFlush(byteBuf);

            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            channel.close();
        }
    }
}
