package com.hellozjf.project.shadowsocks.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理sslocal客户端发送过来的数据
 */
@Slf4j
public class CilentHandler extends ChannelInboundHandlerAdapter {

    /**
     * 目标channel
     */
    private Channel targetChannel;

    public CilentHandler(Channel targetChannel) {
        this.targetChannel = targetChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ctx == null || msg == null || !(msg instanceof ByteBuf)) {
            return;
        }

        ByteBuf byteBuf = (ByteBuf) msg;
        if (targetChannel == null) {
            log.error("target未连接");
            return;
        }
        // 从客户端收到的数据直接转发到目标去
        targetChannel.writeAndFlush(byteBuf);
    }
}
