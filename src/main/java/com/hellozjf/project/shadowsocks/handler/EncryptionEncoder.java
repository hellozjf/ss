package com.hellozjf.project.shadowsocks.handler;

import com.hellozjf.project.shadowsocks.util.CryptUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
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
public class EncryptionEncoder extends MessageToByteEncoder<ByteBuf> {

    private AEADBlockCipher cipher;
    private byte[] key;
    private byte[] subkey;
    private byte[] decNonce = new byte[CryptUtils.getNonceLength()];
    private byte[] encNonce = new byte[CryptUtils.getNonceLength()];

    public EncryptionEncoder(String password) {
        key = CryptUtils.getKey(password);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {

        // 防御性编程
        if (ctx == null || in == null || out == null) {
            return;
        }

        if (cipher == null) {
            if (in.readableBytes() < 32) {
                // 不满32字节，说明连盐都没有
                return;
            }
            // todo aes-256-gcm是32字节的盐长度
            byte[] salt = new byte[32];
            in.readBytes(salt);
            subkey = CryptUtils.genSubkey(salt, key);
            cipher = new GCMBlockCipher(new AESEngine());
        }

        // 将输入的数据进行加密
        CryptUtils.encrypt(in, out, cipher, encNonce, subkey);
    }
}
