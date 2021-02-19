package com.hellozjf.project.shadowsocks.handler;

import cn.hutool.core.util.HexUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hellozjf.project.shadowsocks.service.CryptService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;

/**
 * 加密的解码器
 * 这个类必须有状态，因为要保存一些会话信息，使用的时候请new一个新的对象
 */
@Slf4j
public class CipherEncoder extends MessageToByteEncoder<ByteBuf> {

    private long threadId;
    private AEADBlockCipher cipher;
    private byte[] key;
    private CryptService cryptService;
    private byte[] nonce;
    private byte[] salt;
    private byte[] subkey;

    public CipherEncoder(long threadId, byte[] key) {
        this(threadId, key, null);
    }

    public CipherEncoder(long threadId, byte[] key, byte[] salt) {
        this.threadId = threadId;
        this.cipher = new GCMBlockCipher(new AESEngine());
        this.key = key;
        this.cryptService = SpringUtil.getBean(CryptService.class);
        this.nonce = new byte[cryptService.getNonceLength()];
        this.salt = salt;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {

        // 防御性编程
        if (ctx == null || in == null || out == null) {
            return;
        }

        if (salt == null) {
            salt = cryptService.randomBytes(cryptService.getSaltLength());
        }
        if (subkey == null) {
            out.writeBytes(salt);
            subkey = cryptService.genSubkey(salt, key);
        }

        if (log.isDebugEnabled()) {
            byte[] bytes = new byte[in.readableBytes()];
            in.getBytes(in.readerIndex(), bytes);
            log.debug("threadId:{} 即将加密: {}", threadId, HexUtil.encodeHexStr(bytes));
        }
        // 将输入的数据进行加密
        cryptService.encrypt(in, out, cipher, nonce, subkey);
        if (log.isDebugEnabled()) {
            byte[] bytes = new byte[out.readableBytes()];
            out.getBytes(out.readerIndex(), bytes);
            log.debug("threadId:{} 加密后的数据: {}", threadId, HexUtil.encodeHexStr(bytes));
        }
    }
}
