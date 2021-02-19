package com.hellozjf.project.shadowsocks.handler;

import com.hellozjf.project.shadowsocks.util.DebugUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 从目标服务器接收到数据
 */
@Slf4j
public class TargetInHandler extends ChannelInboundHandlerAdapter {

    /**
     * sslocal的channel
     */
    private Channel clientChannel;

    /**
     * 所属线程ID
     */
    private long threadId;

    public TargetInHandler(Channel clientChannel, long threadId) {
        this.clientChannel = clientChannel;
        this.threadId = threadId;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 防御性编程
        if (ctx == null || msg == null || !(msg instanceof ByteBuf)) {
            return;
        }

        ByteBuf byteBuf = (ByteBuf) msg;
        if (clientChannel == null) {
            log.error("threadId:{} client未连接", threadId);
            return;
        }
        if (byteBuf.readableBytes() == 0) {
            return;
        }

        DebugUtils.printByteBufInfo(threadId, byteBuf, "target->client");

        // 从客户端收到的数据直接转发到目标去
        try {
            clientChannel.writeAndFlush(byteBuf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("threadId:{} 捕获到异常 {}", threadId, cause.getMessage());
        ctx.channel().close();
    }
}
