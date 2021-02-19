package com.hellozjf.project.shadowsocks.handler;

import com.hellozjf.project.shadowsocks.util.DebugUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理sslocal客户端发送过来的数据
 */
@Slf4j
public class CilentInHandler extends ChannelInboundHandlerAdapter {

    /**
     * 线程ID，用于调试
     */
    private long threadId;

    /**
     * 目标channel
     */
    private Channel targetChannel;

    public CilentInHandler(Channel targetChannel, long threadId) {
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
        if (byteBuf.readableBytes() == 0) {
            return;
        }
        // 从客户端收到的数据直接转发到目标去
        DebugUtils.printByteBufInfo(threadId, byteBuf, "client->target");
        try {
            targetChannel.writeAndFlush(byteBuf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("threadId:{} 捕获到异常 {}", threadId, cause.getMessage());
    }
}
