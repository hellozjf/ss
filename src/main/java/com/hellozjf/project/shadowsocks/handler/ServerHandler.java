package com.hellozjf.project.shadowsocks.handler;

import cn.hutool.core.io.FileUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 从服务端接收到数据
 */
@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] sendBytes = new byte[msg.readableBytes()];
        msg.readBytes(sendBytes);
//        log.info("server say: {}", new String(sendBytes, CharsetUtil.UTF_8));
        FileUtil.writeBytes(sendBytes, new File("hello"));
    }
}
