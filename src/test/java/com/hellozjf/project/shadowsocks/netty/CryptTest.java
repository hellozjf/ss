package com.hellozjf.project.shadowsocks.netty;

import cn.hutool.core.util.HexUtil;
import com.hellozjf.project.shadowsocks.BaseTest;
import com.hellozjf.project.shadowsocks.handler.CipherDecoder;
import com.hellozjf.project.shadowsocks.handler.CipherEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;

/**
 * 加解密测试
 */
@Slf4j
public class CryptTest extends BaseTest {

    @Test
    public void test() {
        String password = "123456";
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new CipherEncoder(password, getSalt()),
                new CipherDecoder(password)
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

    @Test
    public void encode() {
        ByteBuf byteBuf = Unpooled.copiedBuffer("helloworld".getBytes(CharsetUtil.UTF_8));
        EmbeddedChannel channel = new EmbeddedChannel(
                new CipherEncoder("123456")
        );
        channel.writeOutbound(byteBuf);
        ByteBuf out = channel.readOutbound();
        byte[] bytes = new byte[out.readableBytes()];
        out.readBytes(bytes);
        log.info("{}", HexUtil.encodeHexStr(bytes));
    }

    @Test
    public void decode() throws UnsupportedEncodingException {
        ByteBuf buf = Unpooled.buffer();
        String hexStr = "bd7a7ea92d479c25e6720161835156bf3203787d491b1362a2f8ba19faa023af89f69518cf05d7dddf7ece9ce6848db55bc9760b0e75aaa9ecd02b3183453455f564fc34abd000beeaf388010d74c83ff34496a031c960013e738ace0ccaa7a818c552ef0ccc2a00eb034b4d652f924dd46c75ae1748870b685528123ac64b961ae13d22ae7cf6877eaaa748225c7e42860fc7fcb635f3ccb8a0f8898c887b5fdc1b652a9f82ffb3b2781c2c4761a27aec14ac920520c8a97e5f8fb1b43f1590003ff813f7108ae35ec74485e0db0dbb5d275bf1ff364721b6c8fca17f648f2d5b147a82c482cbc79ca9ee182f70c013af6b9ed7e5a742da7748771086516644c63017aab6a0593379d2da1e597196776a8f5783da6df5a78ef07e4d155dafff26f50582a08a55bd2654b5bf006111f38b763fcf8463d5d54f9b6fa02c21341e2b49d09ca22bb43f1315d4c2ddace2bfc9a7fe67e9852089870ce91d0a41cb42d6ef2cecad59638cf7d69f03ef5838de2fdfc9ada080a58223c28829c2e02cb256f110cb098d65ed098cf0a8f3b735ed449ae7cc9847ca7f3d6c50b1669567075b0fef423724e94a6704ac5306d29a4ee44f31d6064cfe15bd7f5ff258010fde5640df03039ce0ac8f910b7bc9bedca31f06edb04fa1c88ba739c73def9580e5c04e97172a254cbac9a2e4bc08247b86847be89ace2351de02a9dbd5f33c3bf526e7ddc6a1318df4c313ff3af7b43cf53f7b00fc43f901978c311a44c50a4ef7a99bd86c8281d9f17024e5724415e04fd978300c05be5e1b52bc2f52ef3d1e013f3b9d9734b7b4584921279657019fb8a934602dd3742aa695340c5b0a86c476fa5f6459e2a3981a4d8cb0e98da18647be55540b0a022b23fda7";
        // cecae3259ab5868717668dc6ec98cc1d97e0b75305a272413a6637eeb2ce7015c79a0985e84e97ce84f28126b7fe2b97b3d40c71b2d0e97af70c5028776b0b33db5591f56ec511fa6804d52303099458038fb82e57ff4d657f37ee6a22d8f83568db2ab44034e91361c7a3c1e25a14f4c0d08ef358324ecc293933d843c605a0e0cc77f5ba2dd0f12b0773cf500ac56a2568665c8157d0bf71efecc186ba7fa7c852c74c7533dece23b850aff298f7c19af0b2828ae083231ecaf81e66697bc0ee62fc5476c241b3dd28c6eb64f531553f4b70f507b501342e239b1e668564d00d101a372c46759d90e950ce104877351151f8d8f1cb14e7eb8d47657c1dfa869ae3627106cf3753145f174a5ce500177005c65024e045650da8ad2112aad76389805b7b099da1b04e44b124ce2f0aa138eb7521483ad61aa74018c56ae541ffcd99415540ec7c1e3c4cc0218dddd4c06269d742fa23d32a9693066bbdc8c24a1ca96bd04b7c672bd33279ddb6254c3dd54afcecf3cdfdcde683106b91a3d89235333c9ee7481d27025704656b1ec02242331caa2d70cd991a2af6e3e61b02c405bb0a09a5ad013d0cc8641e378e7d18e9eaf6d4bae7d5c199e46616f76c30dfee237d050a8393ad39bceb14b36b3d16864d0749d4dd651c7f4d95a316099e59e9472aa501debca48de93a2c777f6c0a5500f8ff1e69276273721093cf993a0f067a57133e9d326901d489e707d21d76b17e74ed583c2cbf9dfc67be537aca9c2bdf6dc29cbde04e5889db6a96db5df8bebd89ed725300d336ad8284c7e54a25766cf061d428e5b7a10acd84cd0066fe416456fff364adb82ce24acc62bf949b84bf6fdb5d60654c6aa2e73f52f45b4e5766f80beb8c2086fe2c
        // f3703a33690d0ae97d4e29808ffbf181bc9d93513a0c1ead31c638b3efdc1c738039eb3ac56d159e95093f757b6a18219568fc2e4badb2ea2a81cf7f934af0f6047170e71c11c014b8dc8d0f425d44b7071ccee557b84ca776f105bc5326d1564495eeaeac977dd4e7784764c193660db9aabe056ad70d1a7a02a54e5f126c80aa9475f10a37e189efc72776eb44e042f0e2edfe510b156d4ad72c83f6dbe00ae92dba81f87420627c4ef08a038584a750f292e5fce5129a11b8f9b737d5075560bc313507a149ebea10439fa6ea060b2cb59398537d977c4e878b92b97218b3d6d377c1dd6ecd46a860fc6b6f84dffe6395be4f1918696552c3ac606e4b64643f07a1450e74b435187df18eca1a782381918463895adc13777e11822e3eb0d44d3ddece9da18f450cde01a5490d1661cf433972bf62343b4de993c869e6527430d33e1aa43dc04e2bc8336a25342b8163d61748a2139691f4e8a41fe3252ab9141189a3c1c9e8070a2a7de522b0fe91c067f05783441d5fc48526e47acb6232aff91eb20208672e9bfda7025c12ab44787aa462eb54af85d9255bec9839d016ca23c660791393e10609ea4b8a493da355fbd30551aa9caf7fa0fd195bf59e307762e663759c2c36fe3b36ce996421af67c11e4637796572612894333cce51bb6763b9afca27d05dad2beb5acacbcf14561a354a5e41f6194b2d15a0f353d0cc86b034e8be7d55ed6ce84130c8ace4e60733f18fa27cb729e6219c484722c46094acc57851c21e9c889c12f0f1d729b8e1eaff5f86d4e3601876a6c8acbff9d935837a73a5daefe6a63fa98c66f1efffbc47f80728b943a11fec73fe5a14133edb6b8998bb7dd688588f4a1f1b14409af1e4407ae74cef822f33
        buf.writeBytes(HexUtil.decodeHex(hexStr));
        EmbeddedChannel channel = new EmbeddedChannel(
                new CipherDecoder("123456")
        );
        channel.writeInbound(buf);
        ByteBuf buf2 = channel.readInbound();
        byte[] bytes = new byte[buf2.readableBytes()];
        buf2.getBytes(0, bytes);
        if (bytes[0] == 0x03) {
            int size = (bytes[1] & 0xff);
            int b1 = (bytes[2 + size] & 0xff);
            int b2 = (bytes[3 + size] & 0xff);
            byte[] address = new byte[size];
            System.arraycopy(bytes, 2, address, 0, size);
            log.info("type = 0x03, size = {}, addr = {}, port = {}", size, new String(address), (b1 << 8) + b2);
            byte[] info = new byte[bytes.length - 2 - size - 2];
            System.arraycopy(bytes, 2 + size + 2, info, 0, bytes.length - 2 - size - 2);
            log.info("{}", HexUtil.encodeHexStr(info));
        }

        ByteBuf buf3 = channel.readInbound();
        bytes = new byte[buf3.readableBytes()];
        buf3.readBytes(bytes);
        log.info("{}", HexUtil.encodeHexStr(bytes));
    }
}
