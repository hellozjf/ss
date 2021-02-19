package com.hellozjf.project.shadowsocks.handler;

import cn.hutool.core.util.HexUtil;
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

    private long threadId;

    /**
     * 目标channel
     */
    private Channel targetChannel;

    public CilentHandler(Channel targetChannel, long threadId) {
        this.targetChannel = targetChannel;
        this.threadId = threadId;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 防御性编程
        if (ctx == null || msg == null || !(msg instanceof ByteBuf)) {
            return;
        }

        ByteBuf byteBuf = (ByteBuf) msg;
        if (targetChannel == null) {
            log.error("threadId:{} target未连接", threadId);
            return;
        }
        // 从客户端收到的数据直接转发到目标去
        if (log.isDebugEnabled()) {
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, bytes);
            log.debug("threadId:{} target write {} data {}", threadId, byteBuf.readableBytes(), HexUtil.encodeHexStr(bytes));
        }
        targetChannel.writeAndFlush(byteBuf);
    }
}
