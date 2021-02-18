package com.hellozjf.project.shadowsocks.handler;

import cn.hutool.core.util.HexUtil;
import com.hellozjf.project.shadowsocks.util.CryptUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;

import java.util.List;

/**
 * 加密的解码器
 * 这个类必须有状态，因为要保存一些会话信息，使用的时候请new一个新的对象
 */
@Slf4j
public class CipherDecoder extends ByteToMessageDecoder {

    private AEADBlockCipher cipher;
    private byte[] key;
    private byte[] subkey;
    private byte[] decNonce = new byte[CryptUtils.getNonceLength()];

    public CipherDecoder(String password) {
        key = CryptUtils.getKey(password);
    }

    /**
     * 报文格式为
     * <p>
     * 2B   DataLen
     * 16B  DataLenTag
     * xxB  DATA(Payload)
     * 16B  DataTag
     *
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        // 防御性编程
        if (ctx == null || in == null || out == null) {
            return;
        }

        if (cipher == null) {
            if (in.readableBytes() < CryptUtils.getSaltLength()) {
                // 不满32字节，说明连盐都没有
                return;
            }
            // todo aes-256-gcm是32字节的盐长度
            byte[] salt = new byte[32];
            in.readBytes(salt);
            subkey = CryptUtils.genSubkey(salt, key);
            cipher = new GCMBlockCipher(new AESEngine());
        }

        // 读取剩余的字节，将它们解密
        try {
            byte[] bytes = new byte[in.readableBytes()];
            in.getBytes(0, bytes);
            log.debug("即将解密: {}", HexUtil.encodeHexStr(bytes));
            CryptUtils.decrypt(in, out, cipher, decNonce, subkey);
        } catch (Exception e) {
            log.error("解密失败了: {}", e.getMessage());
            in.resetReaderIndex();
            ctx.channel().close().sync();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("e = {}", cause.getMessage());
    }
}
