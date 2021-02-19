package com.hellozjf.project.shadowsocks.handler;

import com.hellozjf.project.shadowsocks.util.DebugUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理sslocal客户端发送过来的数据
 */
@Slf4j
public class ClientInHandler extends ChannelInboundHandlerAdapter {

    /**
     * 线程ID，用于调试
     */
    private long threadId;

    /**
     * 目标channel
     */
    private Channel targetChannel;

    /**
     * 这里list保存目标channel还没连接上时收到的数据
     * 等目标channel连接上之后就把这些数据发送给target
     */
    private List<ByteBuf> unsendByteBufList = new ArrayList<>();

    public ClientInHandler(long threadId) {
        this.threadId = threadId;
    }

    /**
     * 等待target连接成功后回调
     * @param targetChannel
     */
    public void setTargetChannel(Channel targetChannel) {
        this.targetChannel = targetChannel;
        // 说明targetChannel连接上了，可以发送之前没发送的ByteBuf了
        synchronized (targetChannel) {
            for (ByteBuf byteBuf : unsendByteBufList) {
                sendToTarget(byteBuf);
            }
            unsendByteBufList.clear();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 防御性编程
        if (ctx == null || msg == null || !(msg instanceof ByteBuf)) {
            return;
        }

        ByteBuf byteBuf = (ByteBuf) msg;
        if (byteBuf.readableBytes() == 0) {
            return;
        }
        if (targetChannel == null) {
            unsendByteBufList.add(byteBuf);
            return;
        }

        // 从客户端收到的数据直接转发到目标去
        synchronized (targetChannel) {
            sendToTarget(byteBuf);
        }
    }

    /**
     * 把数据包发送给target
     * @param byteBuf
     */
    private void sendToTarget(ByteBuf byteBuf) {
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
        ctx.channel().close();
    }
}
