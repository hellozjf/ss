package com.hellozjf.project.shadowsocks.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 从目标服务器接收到数据
 */
@Slf4j
public class TargetHandler extends ChannelInboundHandlerAdapter {

    /**
     * sslocal的channel
     */
    private Channel clientHandler;

    public TargetHandler(Channel clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 防御性编程
        if (ctx == null || msg == null || !(msg instanceof ByteBuf)) {
            return;
        }

        ByteBuf byteBuf = (ByteBuf) msg;
        if (clientHandler == null) {
            log.error("client未连接");
            return;
        }
        // 从客户端收到的数据直接转发到目标去
        clientHandler.writeAndFlush(byteBuf);
    }
}
