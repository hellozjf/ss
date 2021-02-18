package com.hellozjf.project.shadowsocks.handler;

import com.hellozjf.project.shadowsocks.util.CryptUtils;
import com.hellozjf.project.shadowsocks.util.IpUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * 加密的解码器
 * 这个类必须有状态，因为要保存一些会话信息，使用的时候请new一个新的对象
 */
@Slf4j
public class EncryptionDecoder extends ByteToMessageDecoder {

    private AEADBlockCipher cipher;
    private byte[] key;
    private byte[] subkey;
    private byte[] decNonce = new byte[CryptUtils.getNonceLength()];

    public EncryptionDecoder(String password) {
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

        // 读取剩余的字节，将它们解密
        CryptUtils.decrypt(in, out, cipher, decNonce, subkey);
    }
}
