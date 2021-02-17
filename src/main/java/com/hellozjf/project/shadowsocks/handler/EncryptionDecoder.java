package com.hellozjf.project.shadowsocks.handler;

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

    private static final int PAYLOAD_SIZE_MASK = 0x3FFF;

    private AEADBlockCipher cipher;
    private byte[] key;
    private byte[] info = "ss-subkey".getBytes(CharsetUtil.UTF_8);
    private byte[] subkey;
    private byte[] decNonce = new byte[getNonceLength()];

    public EncryptionDecoder(String password) {
        key = getKey(password);
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
        if (cipher == null) {
            if (in.readableBytes() < 32) {
                // 不满32字节，说明连盐都没有
                return;
            }
            // todo aes-256-gcm是32字节的盐长度
            byte[] salt = new byte[32];
            in.readBytes(salt);
            subkey = genSubkey(salt);
            cipher = new GCMBlockCipher(new AESEngine());
        }

        // 读取剩余的字节，将它们解密
        decrypt(in, out);
    }

    /**
     * 解密
     *
     * @param in
     * @param out
     * @return
     */
    private void decrypt(ByteBuf in, List<Object> out) throws InvalidCipherTextException {
        byte[] decBuffer = new byte[PAYLOAD_SIZE_MASK + getTagLength()];
        while (true) {
            // [2B   DataLen][16B  DataLenTag]
            int wantLen = 2 + getTagLength();
            // 做一下标记，这样如果后面字节数不够也能回退
            in.markReaderIndex();
            if (in.readableBytes() < wantLen) {
                // 不够处理了，返回就好
                return;
            }
            in.readBytes(decBuffer, 0, wantLen);
            cipher.init(false, getCipherParameters());
            int processBytes = cipher.processBytes(decBuffer, 0, 2 + getTagLength(), decBuffer, 0);
            cipher.doFinal(decBuffer, processBytes);
            increment(decNonce);

            // [DataLen字节   DATA(PAYLOAD)][16B  DataTag]
            int size = (decBuffer[0] << 8) + decBuffer[1];
            if (size == 0) {
                // todo exists?
                log.error("size == 0");
                return;
            }
            wantLen = size + getTagLength();
            if (in.readableBytes() < wantLen) {
                // 可读字节数不够，回滚读指针
                in.resetReaderIndex();
                return;
            }
            in.readBytes(decBuffer, 2 + getTagLength(), wantLen);
            cipher.init(false, getCipherParameters());
            processBytes = cipher.processBytes(decBuffer, 2 + getTagLength(), size + getTagLength(), decBuffer, 2 + getTagLength());
            cipher.doFinal(decBuffer, 2 + getTagLength() + processBytes);
            increment(decNonce);

            // 只需要中间的DATA数据
            ByteBuf decByteBuf = Unpooled.copiedBuffer(decBuffer, 2 + getTagLength(), size);
            out.add(decByteBuf);
        }
    }

    private void increment(byte[] nonce) {
        for (int i = 0; i < nonce.length; i++) {
            ++nonce[i];
            if (nonce[i] != 0) {
                break;
            }
        }
    }

    private CipherParameters getCipherParameters() {
        byte[] nonce = Arrays.copyOf(decNonce, getNonceLength());
        return new AEADParameters(
                new KeyParameter(subkey),
                getTagLength() * 8,
                nonce
        );
    }

    private byte[] genSubkey(byte[] salt) {
        HKDFBytesGenerator hkdf = new HKDFBytesGenerator(new SHA1Digest());
        hkdf.init(new HKDFParameters(key, salt, info));
        // todo aes-256-gcm是32字节
        byte[] okm = new byte[32];
        hkdf.generateBytes(okm, 0, 32);
        return okm;
    }

    private byte[] getKey(String password) {
        MessageDigest messageDigest = null;
        // todo aes-256-gcm是32字节长度
        byte[] key = new byte[32];
        byte[] passwordBytes = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            passwordBytes = password.getBytes(CharsetUtil.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            log.error("e = {}", e.getMessage());
        }

        byte[] hash = null;
        byte[] temp = null;
        for (int i = 0; i < key.length; ) {
            if (i == 0) {
                hash = messageDigest.digest(passwordBytes);
                temp = new byte[hash.length + passwordBytes.length];
            } else {
                System.arraycopy(hash, 0, temp, 0, hash.length);
                System.arraycopy(passwordBytes, 0, temp, hash.length, passwordBytes.length);
                hash = messageDigest.digest(temp);
            }
            System.arraycopy(hash, 0, key, i, hash.length);
            i += hash.length;
        }

        return key;
    }

    private int getTagLength() {
        return 16;
    }

    private static int getNonceLength() {
        return 12;
    }
}
