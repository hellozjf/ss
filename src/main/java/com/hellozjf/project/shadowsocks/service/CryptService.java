package com.hellozjf.project.shadowsocks.service;

import io.netty.buffer.ByteBuf;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.AEADCipher;

import java.util.List;

public interface CryptService {

    /**
     * 加密
     * @param in
     * @param out
     * @param cipher
     * @param encNonce
     * @param subkey
     * @throws InvalidCipherTextException
     */
    void encrypt(ByteBuf in, ByteBuf out, AEADCipher cipher, byte[] encNonce, byte[] subkey) throws InvalidCipherTextException;

    /**
     * 解密
     * @param in
     * @param out
     * @param cipher
     * @param decNonce
     * @param subkey
     * @throws InvalidCipherTextException
     */
    void decrypt(ByteBuf in, List<Object> out, AEADCipher cipher, byte[] decNonce, byte[] subkey) throws InvalidCipherTextException;

    /**
     * 获取nonce长度
     * @return
     */
    int getNonceLength();

    /**
     * 获取subkey
     * @param salt
     * @param key
     * @return
     */
    byte[] genSubkey(byte[] salt, byte[] key);

    /**
     * 获取key
     * @param password
     * @return
     */
    byte[] getKey(String password);

    /**
     * 获取盐长度
     * @return
     */
    int getSaltLength();

    /**
     * 获取标签长度
     * @return
     */
    int getTagLength();

    /**
     * 获取随机字节数组
     * @param size
     * @return
     */
    byte[] randomBytes(int size);
}
