package com.hellozjf.project.shadowsocks.util;

import cn.hutool.core.util.HexUtil;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 调试打印工具
 */
@Slf4j
public class DebugUtils {

    /**
     * 打印ByteBuf信息
     * @param threadId
     * @param byteBuf
     */
    public static void printByteBufInfo(long threadId, ByteBuf byteBuf, String tip) {
        if (log.isDebugEnabled()) {
            if (log.isTraceEnabled()) {
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.getBytes(byteBuf.readerIndex(), bytes);
                log.trace("threadId:{} {} {} {}", threadId, tip, byteBuf.readableBytes(), HexUtil.encodeHexStr(bytes));
            } else {
                log.debug("threadId:{} {} {}", threadId, tip, byteBuf.readableBytes());
            }
        }
    }

    /**
     * 打印ByteBuf信息
     * @param threadId
     * @param list
     */
    public static void printByteBufInfoList(long threadId, List list, String tip) {
        for (Object o : list) {
            printByteBufInfo(threadId, (ByteBuf) o, tip);
        }
    }
}
