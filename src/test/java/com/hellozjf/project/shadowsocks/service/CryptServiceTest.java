package com.hellozjf.project.shadowsocks.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import com.hellozjf.project.shadowsocks.BaseTest;
import com.hellozjf.project.shadowsocks.handler.CipherDecoder;
import com.hellozjf.project.shadowsocks.handler.CipherEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class CryptServiceTest extends BaseTest {

    @Autowired
    private CryptService cryptService;

    @Test
    public void test() {
        log.info("cryptService = {}", cryptService);
    }

    @Test
    public void genSubkey() {
    }

    @Test
    public void getKey() {
        byte[] key = cryptService.getKey("123456");
        log.info("key = {}", HexUtil.encodeHexStr(key));
    }

    @Test
    public void enc() throws Exception {
        String str = FileUtil.readString(new File("tmp/str"), CharsetUtil.UTF_8);
        byte[] key = cryptService.getKey("123456");
        EmbeddedChannel channel = new EmbeddedChannel(
                new CipherEncoder(-1, key, HexUtil.decodeHex("8596a19183555a3c0bde9154e0c7843b8eca0a99062c4a8dad5e2ed2449f2a52"))
        );
        ByteBuf byteBuf = Unpooled.wrappedBuffer(str.getBytes(CharsetUtil.UTF_8));
        channel.writeOutbound(byteBuf);

        File file = new File("tmp/encode_java");
        FileUtil.writeString("", file, CharsetUtil.UTF_8);
        while (true) {
            byteBuf = channel.readOutbound();
            if (byteBuf == null || byteBuf.readableBytes() == 0) {
                break;
            }
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            FileUtil.appendString(HexUtil.encodeHexStr(bytes), file, CharsetUtil.UTF_8);
        }
    }

    @Test
    public void dec() throws Exception {

        String hexStr = FileUtil.readString(new File("tmp/encode"), CharsetUtil.UTF_8);

        byte[] key = cryptService.getKey("123456");
        EmbeddedChannel channel = new EmbeddedChannel(
                new CipherDecoder(-1, key)
        );
        ByteBuf byteBuf = Unpooled.wrappedBuffer(HexUtil.decodeHex(hexStr));
        channel.writeInbound(byteBuf);

        FileUtil.writeString("", new File("tmp/decode_java"), CharsetUtil.UTF_8);
        while (true) {
            byteBuf = channel.readInbound();
            if (byteBuf == null || byteBuf.readableBytes() == 0) {
                break;
            }
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            String decode = new String(bytes, CharsetUtil.UTF_8);
            log.info("{}", decode);

            FileUtil.appendString(decode, new File("tmp/decode_java"), CharsetUtil.UTF_8);
        }
    }

    @Test
    public void f() {
        File file = new File("str");
        StringBuilder sb = new StringBuilder();
        for (char c = 'a'; c <= 'z'; c++) {
            sb.append(c);
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            sb.append(c);
        }
        for (char c = '0'; c <= '9'; c++) {
            sb.append(c);
        }

        FileUtil.writeString("", file, CharsetUtil.UTF_8);
        for (int i = 0; i < 1024; i++) {
            FileUtil.appendString(sb.toString(), file, CharsetUtil.UTF_8);
        }
    }
}