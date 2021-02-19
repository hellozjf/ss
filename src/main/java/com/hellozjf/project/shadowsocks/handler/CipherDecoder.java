package com.hellozjf.project.shadowsocks.handler;

import cn.hutool.core.util.HexUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hellozjf.project.shadowsocks.service.CryptService;
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
    private CryptService cryptService;
    private byte[] subkey;
    private byte[] nonce;

    public CipherDecoder(byte[] key) {
        this(key, null);
    }

    public CipherDecoder(byte[] key, byte[] salt) {
        this.cipher = new GCMBlockCipher(new AESEngine());
        this.key = key;
        this.cryptService = SpringUtil.getBean(CryptService.class);
        if (salt != null) {
            this.subkey = cryptService.genSubkey(salt, key);
        }
        this.nonce = new byte[cryptService.getNonceLength()];
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

        if (subkey == null) {
            if (in.readableBytes() < cryptService.getSaltLength()) {
                // 说明连盐都没有
                return;
            }
            byte[] salt = new byte[cryptService.getSaltLength()];
            in.readBytes(salt);
            log.debug("解密的盐: {}", HexUtil.encodeHexStr(salt));
            subkey = cryptService.genSubkey(salt, key);
        }

        // 读取剩余的字节，将它们解密
        try {
            byte[] bytes = new byte[in.readableBytes()];
            in.getBytes(in.readerIndex(), bytes);
            log.debug("即将解密: {}", HexUtil.encodeHexStr(bytes));
            cryptService.decrypt(in, out, cipher, nonce, subkey);
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
