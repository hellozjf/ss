package com.hellozjf.project.shadowsocks.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

/**
 * 加解密工具
 */
@Slf4j
public class CryptUtils {

    private static final int PAYLOAD_SIZE_MASK = 0x3FFF;
    private static byte[] info = "ss-subkey".getBytes(CharsetUtil.UTF_8);

    /**
     * 加密
     * @param in
     * @param out
     * @param cipher
     * @param encNonce
     * @param subkey
     * @throws InvalidCipherTextException
     */
    public static void encrypt(ByteBuf in, ByteBuf out, AEADCipher cipher, byte[] encNonce, byte[] subkey) throws InvalidCipherTextException {
        byte[] encBuffer = new byte[2 + getTagLength() + PAYLOAD_SIZE_MASK + getTagLength()];
        int nr = Math.min(in.readableBytes(), PAYLOAD_SIZE_MASK);
        // 先写两字节长度
        encBuffer[0] = (byte) (nr >> 8);
        encBuffer[1] = (byte) nr;
        // 芯片设置为加密
        cipher.init(true, getCipherParameters(encNonce, subkey));
        // 加密前两字节长度
        int processBytes = cipher.processBytes(encBuffer, 0, 2, encBuffer, 0);
        // 加上tag
        cipher.doFinal(encBuffer, processBytes);
        // out写出长度和tag
        out.writeBytes(encBuffer, 0, 2 + getTagLength());
        // encNonce++
        increment(encNonce);
        // 再从in中读取nr字节
        in.readBytes(encBuffer, 2 + getTagLength(), nr);

        // 以同样的方式加密数据内容，并写上tag，输出到out
        cipher.init(true, getCipherParameters(encNonce, subkey));
        processBytes = cipher.processBytes(encBuffer, 2 + getTagLength(), nr, encBuffer, 2 + getTagLength());
        cipher.doFinal(encBuffer, 2 + getTagLength() + processBytes);
        increment(encNonce);
        out.writeBytes(encBuffer, 2 + getTagLength(), nr + getTagLength());
    }

    /**
     * 解密
     *
     * @param in
     * @param out
     * @return
     */
    public static void decrypt(ByteBuf in, List<Object> out, AEADCipher cipher, byte[] decNonce, byte[] subkey) throws InvalidCipherTextException {
        byte[] decBuffer = new byte[2 + getTagLength() + PAYLOAD_SIZE_MASK + getTagLength()];
        ByteBuf decByteBuf = null;
        while (true) {
            // [2B   DataLen][16B  DataLenTag]
            int wantLen = 2 + getTagLength();
            // 做一下标记，这样如果后面字节数不够也能回退
            in.markReaderIndex();
            if (in.readableBytes() < wantLen) {
                // 不够处理了，返回就好
                break;
            }
            in.readBytes(decBuffer, 0, wantLen);
            cipher.init(false, getCipherParameters(decNonce, subkey));
            int processBytes = cipher.processBytes(decBuffer, 0, 2 + getTagLength(), decBuffer, 0);
            cipher.doFinal(decBuffer, processBytes);

            // [DataLen字节   DATA(PAYLOAD)][16B  DataTag]
            int size = IpUtils.parseIntFromTwoBytes(decBuffer[0], decBuffer[1]);
            if (size == 0) {
                // todo exists?
                log.error("size == 0");
                break;
            }
            wantLen = size + getTagLength();
            if (in.readableBytes() < wantLen) {
                // 可读字节数不够，回滚读指针
                in.resetReaderIndex();
                break;
            }
            increment(decNonce);

            in.readBytes(decBuffer, 2 + getTagLength(), wantLen);
            cipher.init(false, getCipherParameters(decNonce, subkey));
            processBytes = cipher.processBytes(decBuffer, 2 + getTagLength(), size + getTagLength(), decBuffer, 2 + getTagLength());
            cipher.doFinal(decBuffer, 2 + getTagLength() + processBytes);
            increment(decNonce);

            // 只需要中间的DATA数据
            if (decByteBuf == null) {
                decByteBuf = Unpooled.buffer();
            }
            decByteBuf.writeBytes(decBuffer, 2 + getTagLength(), size);
        }
        if (decByteBuf != null) {
            out.add(decByteBuf);
        }
    }

    public static int getNonceLength() {
        return 12;
    }

    public static byte[] genSubkey(byte[] salt, byte[] key) {
        HKDFBytesGenerator hkdf = new HKDFBytesGenerator(new SHA1Digest());
        hkdf.init(new HKDFParameters(key, salt, info));
        // todo aes-256-gcm是32字节
        byte[] okm = new byte[32];
        hkdf.generateBytes(okm, 0, 32);
        return okm;
    }

    public static byte[] getKey(String password) {
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

    public static int getSaltLength() {
        return 32;
    }

    public static byte[] randomBytes(int size) {
        byte[] bytes = new byte[size];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    private static void increment(byte[] nonce) {
        for (int i = 0; i < nonce.length; i++) {
            ++nonce[i];
            if (nonce[i] != 0) {
                break;
            }
        }
    }

    private static int getTagLength() {
        return 16;
    }

    private static CipherParameters getCipherParameters(byte[] rawNonce, byte[] subkey) {
        byte[] nonce = Arrays.copyOf(rawNonce, getNonceLength());
        return new AEADParameters(
                new KeyParameter(subkey),
                getTagLength() * 8,
                nonce
        );
    }
}
