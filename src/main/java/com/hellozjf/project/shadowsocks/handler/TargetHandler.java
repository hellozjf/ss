package com.hellozjf.project.shadowsocks.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 从目标服务器接收到数据
 */
@Slf4j
public class TargetHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * sslocal的channel
     */
    private Channel clientHandler;

    public TargetHandler(Channel clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] sendBytes = new byte[msg.readableBytes()];
        msg.readBytes(sendBytes);
        ByteBuf byteBuf = Unpooled.copiedBuffer(sendBytes);
        clientHandler.writeAndFlush(byteBuf);
    }
}
