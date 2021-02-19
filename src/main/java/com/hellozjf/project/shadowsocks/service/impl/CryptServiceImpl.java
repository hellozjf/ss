package com.hellozjf.project.shadowsocks.service.impl;

import com.hellozjf.project.shadowsocks.service.CryptService;
import com.hellozjf.project.shadowsocks.util.IpUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

/**
 * 加密服务
 */
@Slf4j
@Service
public class CryptServiceImpl implements CryptService {

    @Autowired
    @Qualifier("md5MessageDigest")
    private MessageDigest md5MessageDigest;

    private final int PAYLOAD_SIZE_MASK = 0x3FFF;
    private byte[] info = "ss-subkey".getBytes(CharsetUtil.UTF_8);

    @Override
    public void encrypt(ByteBuf in, ByteBuf out, AEADCipher cipher, byte[] encNonce, byte[] subkey) throws InvalidCipherTextException {
        while (in.readableBytes() > 0) {
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
    }

    @Override
    public void decrypt(ByteBuf in, List<Object> out, AEADCipher cipher, byte[] decNonce, byte[] subkey) throws InvalidCipherTextException {
        byte[] decBuffer = new byte[2 + getTagLength() + PAYLOAD_SIZE_MASK + getTagLength()];
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
            ByteBuf decByteBuf = Unpooled.copiedBuffer(decBuffer, 2 + getTagLength(), size);
            out.add(decByteBuf);
        }
    }

    @Override
    public int getNonceLength() {
        // todo aes-256-gcm是12字节
        return 12;
    }

    @Override
    public int getKeyLength() {
        // todo aes-256-gcm是32字节
        return 32;
    }

    @Override
    public int getSubkeyLength() {
        // todo aes-256-gcm是32字节
        return 32;
    }

    @Override
    public int getSaltLength() {
        // todo aes-256-gcm是32字节的盐长度
        return 32;
    }

    @Override
    public int getTagLength() {
        // todo aes-256-gcm是16字节的盐长度
        return 16;
    }

    @Override
    public byte[] genSubkey(byte[] salt, byte[] key) {
        HKDFBytesGenerator hkdf = new HKDFBytesGenerator(new SHA1Digest());
        hkdf.init(new HKDFParameters(key, salt, info));
        byte[] okm = new byte[getSubkeyLength()];
        hkdf.generateBytes(okm, 0, getSubkeyLength());
        return okm;
    }

    @Override
    public byte[] getKey(String password) {
        byte[] key = new byte[getKeyLength()];
        byte[] passwordBytes = password.getBytes(CharsetUtil.UTF_8);

        byte[] hash = null;
        byte[] temp = null;
        for (int i = 0; i < key.length; ) {
            if (i == 0) {
                hash = md5MessageDigest.digest(passwordBytes);
                temp = new byte[hash.length + passwordBytes.length];
            } else {
                System.arraycopy(hash, 0, temp, 0, hash.length);
                System.arraycopy(passwordBytes, 0, temp, hash.length, passwordBytes.length);
                hash = md5MessageDigest.digest(temp);
            }
            System.arraycopy(hash, 0, key, i, hash.length);
            i += hash.length;
        }

        return key;
    }

    @Override
    public byte[] randomBytes(int size) {
        byte[] bytes = new byte[size];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    private void increment(byte[] nonce) {
        for (int i = 0; i < nonce.length; i++) {
            ++nonce[i];
            if (nonce[i] != 0) {
                break;
            }
        }
    }

    private CipherParameters getCipherParameters(byte[] rawNonce, byte[] subkey) {
        byte[] nonce = Arrays.copyOf(rawNonce, getNonceLength());
        return new AEADParameters(
                new KeyParameter(subkey),
                getTagLength() * 8,
                nonce
        );
    }
}
