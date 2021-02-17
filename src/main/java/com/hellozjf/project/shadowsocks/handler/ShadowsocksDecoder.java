package com.hellozjf.project.shadowsocks.handler;

import com.hellozjf.project.shadowsocks.util.IpUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * shadowsocks的解码器
 * 这个东西主要是解析出shadowsocks的头部（1字节类型，N字节地址，2字节端口）
 * 类型1：地址是4字节IPv4
 * 类型3：地址第一个字节是地址长度，后面是字符串地址
 * 类型4：地址是16字节IPv6
 */
@Slf4j
public class ShadowsocksDecoder extends ByteToMessageDecoder {

    /**
     * 1字节类型
     */
    private int type;
    /**
     * N字节地址
     */
    private String address;
    /**
     * 2字节端口
     */
    private int port;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!StringUtils.hasLength(address)) {
            if (!initAddress(in)) {
                return;
            }
        }
        // 至此，说明地址已经解析出来了，打印下看看效果
        log.info("thread={} type={}, address={}, port={}", Thread.currentThread(), type, address, port);
    }

    /**
     * 初始化地址信息
     * @param in
     */
    private boolean initAddress(ByteBuf in) {
        if (in.readableBytes() < 4) {
            // 不满字节，说明包不完整
            return false;
        }
        // 记录读指针
        in.markReaderIndex();
        // 需要读取出SS目标地址信息
        // 1. 读取类型
        type = in.readByte();
        // 2. 读取地址
        switch (type) {
            case 0x01:
                if (in.readableBytes() < 4 + 2) {
                    in.resetReaderIndex();
                    return false;
                }
                // 地址是IPv4
                byte[] ipv4 = new byte[4];
                for (int i = 0; i < ipv4.length; i++) {
                    ipv4[i] = in.readByte();
                }
                address = IpUtils.parseIpv4(ipv4);
                break;
            case 0x03:
                int length = (in.readByte() & 0xff);
                if (in.readableBytes() < length + 2) {
                    in.resetReaderIndex();
                    return false;
                }
                // 地址是字符串
                byte[] addr = new byte[length];
                for (int i = 0; i < length; i++) {
                    addr[i] = in.readByte();
                }
                address = new String(addr, CharsetUtil.UTF_8);
                break;
            case 0x04:
                if (in.readableBytes() < 16 + 2) {
                    in.resetReaderIndex();
                    return false;
                }
                // 地址是IPv6
                byte[] ipv6 = new byte[16];
                for (int i = 0; i < ipv6.length; i++) {
                    ipv6[i] = in.readByte();
                }
                address = IpUtils.parseIpv6(ipv6);
                break;
            default:
                log.error("未知类型{}", type);
                return false;
        }
        // 3. 读取端口
        int p1 = (in.readByte() & 0xff);
        int p2 = (in.readByte() & 0xff);
        port = (p1 << 8) + p2;
        return true;
    }
}
