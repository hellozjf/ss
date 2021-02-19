package com.hellozjf.project.shadowsocks.handler;

import com.hellozjf.project.shadowsocks.util.DebugUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TargetOutHandler extends ChannelOutboundHandlerAdapter {

    private long threadId;

    public TargetOutHandler(long threadId) {
        this.threadId = threadId;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        DebugUtils.printByteBufInfo(threadId, (ByteBuf) msg, "to target");
        super.write(ctx, msg, promise);
    }
}
