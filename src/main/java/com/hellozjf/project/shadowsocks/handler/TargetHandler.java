package com.hellozjf.project.shadowsocks.handler;

import cn.hutool.core.util.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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

    /**
     * 所属线程ID
     */
    private long threadId;

    public TargetHandler(Channel clientHandler, long threadId) {
        this.clientHandler = clientHandler;
        this.threadId = threadId;
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
        log.debug("threadId:{} client write {}", threadId, byteBuf.readableBytes());
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0, bytes);
        log.debug("threadId:{} data {}", threadId, HexUtil.encodeHexStr(bytes));
        clientHandler.writeAndFlush(byteBuf);
    }
}
