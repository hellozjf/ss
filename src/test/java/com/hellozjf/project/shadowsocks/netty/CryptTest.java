package com.hellozjf.project.shadowsocks.netty;

import cn.hutool.core.util.HexUtil;
import com.hellozjf.project.shadowsocks.BaseTest;
import com.hellozjf.project.shadowsocks.handler.EncryptionDecoder;
import com.hellozjf.project.shadowsocks.handler.EncryptionEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

/**
 * 加解密测试
 */
@Slf4j
public class CryptTest extends BaseTest {

    @Test
    public void test() {
        String password = "123456";
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new EncryptionEncoder(password, getSalt()),
                new EncryptionDecoder(password)
        );

        ByteBuf writeOutBuf = Unpooled.buffer();
        String outMessage = "helloworld";
        writeOutBuf.writeBytes(outMessage.getBytes(CharsetUtil.UTF_8));
        embeddedChannel.writeOutbound(writeOutBuf);

        ByteBuf readOutBuf = embeddedChannel.readOutbound();
        byte[] readBuf = new byte[readOutBuf.readableBytes()];
        readOutBuf.markReaderIndex();
        log.info("size = {}", readOutBuf.readableBytes());
        readOutBuf.readBytes(readBuf);
        log.info("{}", HexUtil.encodeHexStr(readBuf));
        readOutBuf.resetReaderIndex();

        ByteBuf writeInBuf = readOutBuf;
        embeddedChannel.writeInbound(writeInBuf);
        ByteBuf readInBuf = embeddedChannel.readInbound();
        readBuf = new byte[readInBuf.readableBytes()];
        readInBuf.readBytes(readBuf);
        String inMessage = new String(readBuf);
        log.info("outMessage = {}", outMessage);
        log.info("inMessage = {}", inMessage);
        Assert.isTrue(outMessage.equals(inMessage), "");
    }

    private byte[] getSalt() {
        byte[] salt = {
                (byte) 61, (byte) 99, (byte) -40, (byte) 47, (byte) 27, (byte) -118, (byte) 38, (byte) -98,
                (byte) -82, (byte) -104, (byte) 86, (byte) 83, (byte) -123, (byte) -19, (byte) 55, (byte) 67,
                (byte) -117, (byte) -10, (byte) -28, (byte) 7, (byte) 112, (byte) 85, (byte) 22, (byte) 122,
                (byte) -8, (byte) 127, (byte) -38, (byte) 78, (byte) 99, (byte) -116, (byte) 111, (byte) 31
        };
        return salt;
    }
}
