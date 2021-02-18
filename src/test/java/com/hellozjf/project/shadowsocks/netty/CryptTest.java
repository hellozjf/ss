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
        byte[] salt = HexUtil.decodeHex("3889bbc3973285bfcebb80ec8c64d0433a1adea4154ee95d2ab2a357fe591a96");
        EmbeddedChannel channel = new EmbeddedChannel(
                new CipherEncoder("123456", salt)
        );
        channel.writeOutbound(byteBuf);
        ByteBuf out = channel.readOutbound();
        byte[] bytes = new byte[out.readableBytes()];
        out.readBytes(bytes);
        log.info("{}", HexUtil.encodeHexStr(bytes));
    }

    @Test
    public void decode() throws UnsupportedEncodingException {
//        ByteBuf buf = Unpooled.buffer();
//        String hexStr = "0dc830370c64089bcd350eb7b32caec443cb07ab6e31f3ddc3059f5396223c63063b2a389d486ef6df13e5795bed17d1169fca51a554f4c20a1fa5bd91a1b1a0a33c47234ca6e50daa00a5fca6d97f2910894d9e25ef44af6ebfc6ee90bc82f1da8ecd6409bced1a30eef08abe57845c5dfd69c113c0fd4d56508561c9884a2f98619032914bc61b8cb80a02eb13796a1066609cca1c1eef3c833000befcfe926574480822ba2e93e3dcdc8c7fd47c824c8753058a95aaff4b579b6fd74c8dbaa66d28d44374a0892fed9ff695c0a925dd94e10394c21f12384ccb28aba6cf6ed42d1d1ad4165faece77b0cec7bae32e30bfb48ec5cd15d2f5e3b89b306349db9971c152db08762737d999a3c21096f7c06cc9b4316bda7ce1ad6dbb3d34bea580de473ea9271f3847cd7a8263d8bf5cc45329df02259654e754b5b33bf01e02a96454e99d89ddb3302ad7a92cd6807074625fe7612b161ba850166691880208bcf303c46bb72b505074725d8e8828a32c1ad41f710cd2d45af4bc8e983a5fa999e50785127c2be61e97d079db04002a71786052304f4aaa592353f4c4e8d332ddd9c538565d11ee28b344b0b862fb253e74e4dfcf7231da5a7b170205509dd1b179f21c8435e2d63ac0f0c1e891e287a75f4316caa32000a28b215badc537cff9c4c3fafb6bf9a3b2380a715e39259441fe53754232ab27d8085bfd2dd4c28b6a4cc9ffdd02f9eb02cc9db1aeadfa9a1bb70e099d66c15cd90d3267112392c6bb826667cf204298a9c44df247d6ab1571305eff8bf984f22124941a1f0034c83088d5dda41b8bcb4af54b4fcac81297f4c35027b4c6af62217d" +
//                "672e31cfbf38a8673741866243f6f898b2cd03099fd91f017497eeea73250730741dada79c4a4e5b6f404b2e200256aa7fe8bf115667bf36d2e55aa37d5386c87d321f1ac305b91d92f478a032fa0f9219b609dbaf" +
//                "a90ea3e0c6dff61f8db187a318c7e0eca9a0fa60b6c46ccb7dff75549ef0a79ff2969a4bcd2850e8c06b7ebd9ec091686798b75f256a6fe809529356bef6b5fa33a2103bcacd64a44458b2ac7bbcd132164fed0a6ca3891d1ed3fb08287b39a5506a713c17e5942b3b4749f30ba94533290c0f16d4d96ed39d6521523ae1ad2c727bc7afd0a04d511e939cca7d1a6d4e00c877c9e7426262a2392c15b9d2a87f252036a7ea64d76ca174b1c239b2272b12861defe3c5b6134051a820ce353c62c9bb7bc754d844ce77c09c5dae5f23b1479ce9074174ab93e186cf6b313bbb414d595331c4ce8039afeae27bafba792df7e1673cd989a7a92ba6ef3b300e46feeab133cf60840cddc2c221ecc56bdb8fdf9f2221cb1d5e1751864ce146b2dbaeba281643cb8f88b40fca602f91394b3c2fe9e12e2b1ba70f65ee2e779c0bd0a0260d13748cc3910c627836858bd6fef6cec21d8f8fdc157c23153951f2c9e41ef21827275d6e0e8ec8266a33fa2536b338fea8dacc988346cb2037f361620c55a6dcf614daf5da937671431f4af3055325b2befac3a3ae0416fc97977cd35ef2913e37f0619abb53507223ac111e14b8bbf60ef1a9b04a6452b120c4ce18566d27ae702f2fcf7a4fb2428aced91299d63c20b6ec11cf2892d10eb19b04b17410ff9a9e64638f2176f02e65e69e591c5d7bb48d13e964b65b8acfe177af6b07400c585f2de45cfa055434909b860cb1c4b0db2b809621727d7cd211e76197dc2adf504372beaf83977f1002f6f0807b260dfd24a2798f14ed7eeebb8ddfe313c4783e8fbf8d31c046ca41fdf719e262f51c38b11daebf56c00d5253f3c294d98f21d663cdbc626160341cb8ebb394e595fdda8faa89ae8440e5d9dba975ecea2f52909d27a6a3ed8e4abeec8a5c2e9b31416ab835a21cc84dc1ef60d4f99cf9cbfe579093fb3e4de32b3fd4a36aa9efece2268add18f5eda5b40a1978f54ddf8f6e0f6f847c76fa6906052969885727f8a3953668490160678349d30e85039f6d1005264547b1816094c333aae2701c0cc468c9c71582b4fdc5a93a5da19f2e54f14ea8b5fe4a8adc9ab4c2ed304b77a7dadf5032572f2a2a59e63e5e0318c660f9de3593e7aea9be442a1dc946744d302b84fd029800c63d8e4dba38ca28a5ca23390eddf34e49d48bd6d43dd10c21059955e1ccaad943b388e22f184de4e3e57e906ca7cb9df9def46d3d4d363f87e9b3fde335034c2f04780dba2023038140522f756e503fb72b1992ba724f93916c4537bf1e11794adaa3108cb62336967ccc9603c5459d6593df873d4aa04a79acacc51084de2355477632207bbe0f8394c291593e955baf2ea7003dfd9a2aeb815455c25e0977435687e84d6f9b498c691df83806afc77ee41aca5b421f3bac6352fe69a3b642e215e455dddce8e323e1b6e2cc9557ba7cb33372c730e02880413c06d47a8c48b43cfd3cf2fc81302c00def43126fa13d888e40b0714ab0a0dd84844d9674aac78143d76019a42e8b50cf82418bd0182dd1b6d566f919422e772155017a6c4a81690dfdfd2ba04f456b275ee25a1c101c3bb2dd7223dd2b60a1306fb64f3d77350df43d894b2f1ca42a7463ebf7faf5b5ad9577b47934fee9b374acd837bcd3788c4851094c07c08b79f93517541259337684b5f7969d6e37a315ba0b2215ff9906ecb7611812f6bfdd130b73a5f05ac369823082ee32c08709130b87cb24bcb623ed2f013850fcf5be7953d31be2b5928ddfb4352a6ddd8214a4a196d6b9d17df9a9a44d6828c62a729791cafea9a038cbda0ef824daf1182f8bdf2c1c864a3b98d5873229b9480ed20b4050713b6936002e585fb46d5967aa0ca64905f3b456085eedfa9d247e575a99f24804895436fe42b294b855a1fd2e8f1f9796a5dbea2ab4eaf4c8ef2c580eee97fc82ec1ab1b9c37add1cc1d46d1776a148618806a5bb1509506148909e31ecab91dfb3a346cf13c20172ad099e48d9ceeb6250bb07149507fc2858c760b6eb6103c1b63835ba7ae67b5a3424949e01af5308bf9a4100ab5135beff98f7f5e89d84443c88a979baabc5c04eff9a1a1f4db2ecb0c94cff6b01b97d96d549aa6d991cfd5499b66ac168d276e2012906c455283ddce4326f74b5dfbdb3700f478ae3ab1922140f78610c1c0ba10e3701a65e58717a13d1cc26803f8504f296e4bd71bfaa664ef79660ef83f734ef6d482527beeee32ce1faa8730012642c97e61fe854c4e12e1c2d40a8d8c47aa22da56dc4526d27728c4ea5a6d438aee686c9aa2f27d1f6b1250ca6cf7b00398adb3619fb5bc11304df61ba1a5c8152321a03020186a45bb80e43e5d15c2e4745653db339f5bd8d5a2db978324add97000cd56f19afd3e225a8e56e5ed321cfc99059f214fb4512ce1a19c888f9d87428babb59e85f66c68188a7f2059ced172bf6f414846ce722e191fd76a08b019757158120fe2f73003114b05e65e4ac684c689ef1490b951d3ab1af5f2405dce72d7aabb6b64d5787a1eb4c2f6a286348bcd2d4bc625081375c196842d8a5979c4e05f6cc2fffca52e98c665a91016854349e409b53688034bfae22dfd8235b8ed2b5add192c40272a6b774f88f54fa6480903d2c99ac329a9d36cf59eefb906ef78cd8f48426d65ae624d6404c5b008a456079449452a9f147c7bbc2b796be353fce8e0fda617c108edcccd265e56bbde4fa058dc240b461ba42634f3deeea0532cb18a226a5a5a545e788f3b807c0c501a0edd70e30a14e80a91f58f68edd4be50b268e1bf3a5c6fce5e72c0a9870b286f8a0fbacfb3043cf0ba352a7a65d50ec931a0e486c4eee3d3b3f727019d86772fb7e05c993d0c383a96a14de8ea30882729d842bba245f4d91d91c461433984e0799590c34f8f008e885e7d8f9ea74ec89dd0e14273265b833893a4381f934a711ffd82e708a8ae8d912a815554f20aded40704c5a078c3e7de01d1dea1fffb71522360c9f24f2f2788947004d3ce39990bdef507ebb4509d7e41139963083eabc44ec9a3f36d4b7c41828eaa6223397adf22f00f87b95da44af22c294a827eb53a42aaefaf4224880ae2b944cc55aa0a34481846ccc64267cc317b659fb042d228c4a85bbea62d1e8043019af9492649362628e98367504ba02a5054dd19c247ed48dc954d8913c5131d80a9274f6f395a4fa3614595b3a4447ede4b9434ee2cc60ba3211a686bf0f5b8efe99402e93849311652529c2bedd059ea415751164fd491916d0a89b18b6018a488be34440cc8c89e23e2a73c57d024b12035bb9b46b4d3b234c12bbaa4b3fb0bceeb2c96443d0944090a220a299d41b9dc95f825ea750741626f988280de19ad9b63c61d6d00371efb3c77e3f37bc88a79441a3349834202e4e031f5c566577d34cc1ad4b0e3819b6172b72375a0d4f7924397f074714407e2099929265e3f3d258eccea005b606c9b9232c7d30cb60a5b6d73f85d68d147776fc48cbac42f514f722014b1a9e1ce21d75006c7246a9ced6ec9764b4f0a5cc38f05b5767ab79d9e36459f48d8acae126ac75391b4a99a4cce8cd6d0f95658b97ad431b51c17330121778f6b123cee6561e636b2e8d0375aaaa1ca7708a1bf9df1013c90a7bac0290df2dee647975035a5de926c0e1fbb4575bdd14b52b43369134cbf6d1ef2d3141b5a4e4730c8ba027bdb23ef02d6cce3197597d535bfe52700c0a17ea87853587c94b81f173efecbacc61bfaf75631f49280dbcea5f4c25628c5be84379c40e7e335885e122dcb31fd6e6186dd1f8082fdd08e28bbf0ac837a72738ea6bf4a558dd24bf9bfee45b732ac69b44b13cd1b6dd82ca18b0f6e4f963e9b6c748aea3c087dea6fa7097337a90d8c62455d91624fd83ccebee8f4574b9dfe5217d9e51ae841c55bcb60179daf414dcaac90ea31aee77f7731ce8d907e11545b79b8e8dd6f246ae5ee7e0754ca725e3151a70c5488ade1eaf3ca292ca5189f336e4f11e0197652e4d8aee36e430e5dd3b60e26a7481db81c500c39aa7fc0e2d096a5dc4a66e60fce65993749a709578444990d95684cbd8974b26d9019be5444b81d2505e59432d6ddcff14bc23b606ace2c5ab8aeae700fe176022981e96cf2c74a1a681a628ede789dcf3cad6478a2f2e33cbcc77c382f041ad7ab2ed2200f383410722f3d331627c6a8c457e967471995b2544b428c2e7639e9449094677ccf6753fae31ae345556e74a15469d39092d8cbc170b59837de5399a748559dfe290399ddb1c69ad192ca2efd7238523cbfe0c8e56c5157dff639f955624e6909369b058f74e5c754421a769e848ec3170f7c78bcebc970233f753d3668794fe7a63a72f88e814736a05967a580b171e4e050a536447c7b1ce1998ce76919d0b2fae8a5169b23f29a46fe0085498fe268b481dfbfee04ac6c092d4265f0f4505d6e0bd117ae601cc6882eb40d85427df6322956ed49fa5023b29fb3f5d071363b061d55983e18dcb43b7b68ce61f246e9b17707be90c9959a04a6503d6a1bc8a8d709e397f429232cbf762d1c4b844a3db5de5f5ec51a754d375597eba43ae022c9ed4af7fee6b5c7a87a13b5031fcbd89b48708cb674d7caa70bff8041795bb07db40209994f6b49da3d16e00f73f35df88ef844c738666fae293eaf76852e3a382d28c7a47b52f97ca48992fa78a38a58c8a9ee688843a9046e0dfb8166a3a43cd5031ce02b091aeaf25c5d24988d2361fb2d531fc70954f0a45163007d5bc959662f3c2553cd97a89877d8749ec3160bf56e6b65eaa1683e645ceaf950f9b8efb18fffdbbd27e2c105f2695d4a55750147de538f824c30077e057dc38fba5df6d27ee0e83f3b4abad4a64f10f6e055a86a7d394cb127a291cc4395c2725da2cd3e724c6908140c817f5ada801155ff7a8b8311b48cfb4b0d0e8b2805751d0a04c107a572450818c513e30172bb30c1d780866b5ee9ab57014cf22764cd7996e3ab6e16cf0e1098a7ef1226d6a9d5df4a9dd007a07d6ce4e84cb446d225014d179ecc1a9f4523aa85bbe032c5ef95313cfba089e8b24a2d9956d07de2c57efb806ca41847cf69247e6fe672073d122b2943182c2a326a3f5a19e43accc048117b194243ccc3fa4422bed7e8de99e65b21f1180ecd7d44b752acd4ce9fd32e355a4d9f44f0dee92f2707e337b173eb9300a3ef335cec9dfdd1dc7cf1a6878510cc52e6bde9feb13d4ac48fa8c7020d0fc85fbc413479966621cf47e2df8b335f7aa363b8573860a57be71a27bf50beaec0";
//        buf.writeBytes(HexUtil.decodeHex(hexStr));
//        EmbeddedChannel channel = new EmbeddedChannel(
//                new CipherDecoder("123456", HexUtil.decodeHex("da7436c0f0b74495adfb2dd55a0cde7e36edfd379af2a724a02f1d46c65a3978"))
//        );
//        channel.writeInbound(buf);

        ByteBuf b1 = Unpooled.copiedBuffer(HexUtil.decodeHex("da7436c0f0b74495adfb2dd55a0cde7e36edfd379af2a724a02f1d46c65a39780dc830370c64089bcd350eb7b32caec443cb07ab6e31f3ddc3059f5396223c63063b2a389d486ef6df13e5795bed17d1169fca51a554f4c20a1fa5bd91a1b1a0a33c47234ca6e50daa00a5fca6d97f2910894d9e25ef44af6ebfc6ee90bc82f1da8ecd6409bced1a30eef08abe57845c5dfd69c113c0fd4d56508561c9884a2f98619032914bc61b8cb80a02eb13796a1066609cca1c1eef3c833000befcfe926574480822ba2e93e3dcdc8c7fd47c824c8753058a95aaff4b579b6fd74c8dbaa66d28d44374a0892fed9ff695c0a925dd94e10394c21f12384ccb28aba6cf6ed42d1d1ad4165faece77b0cec7bae32e30bfb48ec5cd15d2f5e3b89b306349db9971c152db08762737d999a3c21096f7c06cc9b4316bda7ce1ad6dbb3d34bea580de473ea9271f3847cd7a8263d8bf5cc45329df02259654e754b5b33bf01e02a96454e99d89ddb3302ad7a92cd6807074625fe7612b161ba850166691880208bcf303c46bb72b505074725d8e8828a32c1ad41f710cd2d45af4bc8e983a5fa999e50785127c2be61e97d079db04002a71786052304f4aaa592353f4c4e8d332ddd9c538565d11ee28b344b0b862fb253e74e4dfcf7231da5a7b170205509dd1b179f21c8435e2d63ac0f0c1e891e287a75f4316caa32000a28b215badc537cff9c4c3fafb6bf9a3b2380a715e39259441fe53754232ab27d8085bfd2dd4c28b6a4cc9ffdd02f9eb02cc9db1aeadfa9a1bb70e099d66c15cd90d3267112392c6bb826667cf204298a9c44df247d6ab1571305eff8bf984f22124941a1f0034c83088d5dda41b8bcb4af54b4fcac81297f4c35027b4c6af62217d"));
        ByteBuf b2 = Unpooled.copiedBuffer(HexUtil.decodeHex("672e31cfbf38a8673741866243f6f898b2cd03099fd91f017497eeea73250730741dada79c4a4e5b6f404b2e200256aa7fe8bf115667bf36d2e55aa37d5386c87d321f1ac305b91d92f478a032fa0f9219b609dbaf"));
        ByteBuf b3 = Unpooled.copiedBuffer(HexUtil.decodeHex("a90ea3e0c6dff61f8db187a318c7e0eca9a0fa60b6c46ccb7dff75549ef0a79ff2969a4bcd2850e8c06b7ebd9ec091686798b75f256a6fe809529356bef6b5fa33a2103bcacd64a44458b2ac7bbcd132164fed0a6ca3891d1ed3fb08287b39a5506a713c17e5942b3b4749f30ba94533290c0f16d4d96ed39d6521523ae1ad2c727bc7afd0a04d511e939cca7d1a6d4e00c877c9e7426262a2392c15b9d2a87f252036a7ea64d76ca174b1c239b2272b12861defe3c5b6134051a820ce353c62c9bb7bc754d844ce77c09c5dae5f23b1479ce9074174ab93e186cf6b313bbb414d595331c4ce8039afeae27bafba792df7e1673cd989a7a92ba6ef3b300e46feeab133cf60840cddc2c221ecc56bdb8fdf9f2221cb1d5e1751864ce146b2dbaeba281643cb8f88b40fca602f91394b3c2fe9e12e2b1ba70f65ee2e779c0bd0a0260d13748cc3910c627836858bd6fef6cec21d8f8fdc157c23153951f2c9e41ef21827275d6e0e8ec8266a33fa2536b338fea8dacc988346cb2037f361620c55a6dcf614daf5da937671431f4af3055325b2befac3a3ae0416fc97977cd35ef2913e37f0619abb53507223ac111e14b8bbf60ef1a9b04a6452b120c4ce18566d27ae702f2fcf7a4fb2428aced91299d63c20b6ec11cf2892d10eb19b04b17410ff9a9e64638f2176f02e65e69e591c5d7bb48d13e964b65b8acfe177af6b07400c585f2de45cfa055434909b860cb1c4b0db2b809621727d7cd211e76197dc2adf504372beaf83977f1002f6f0807b260dfd24a2798f14ed7eeebb8ddfe313c4783e8fbf8d31c046ca41fdf719e262f51c38b11daebf56c00d5253f3c294d98f21d663cdbc626160341cb8ebb394e595fdda8faa89ae8440e5d9dba975ecea2f52909d27a6a3ed8e4abeec8a5c2e9b31416ab835a21cc84dc1ef60d4f99cf9cbfe579093fb3e4de32b3fd4a36aa9efece2268add18f5eda5b40a1978f54ddf8f6e0f6f847c76fa6906052969885727f8a3953668490160678349d30e85039f6d1005264547b1816094c333aae2701c0cc468c9c71582b4fdc5a93a5da19f2e54f14ea8b5fe4a8adc9ab4c2ed304b77a7dadf5032572f2a2a59e63e5e0318c660f9de3593e7aea9be442a1dc946744d302b84fd029800c63d8e4dba38ca28a5ca23390eddf34e49d48bd6d43dd10c21059955e1ccaad943b388e22f184de4e3e57e906ca7cb9df9def46d3d4d363f87e9b3fde335034c2f04780dba2023038140522f756e503fb72b1992ba724f93916c4537bf1e11794adaa3108cb62336967ccc9603c5459d6593df873d4aa04a79acacc51084de2355477632207bbe0f8394c291593e955baf2ea7003dfd9a2aeb815455c25e0977435687e84d6f9b498c691df83806afc77ee41aca5b421f3bac6352fe69a3b642e215e455dddce8e323e1"));
        ByteBuf b4 = Unpooled.copiedBuffer(HexUtil.decodeHex("b6e2cc9557ba7cb33372c730e02880413c06d47a8c48b43cfd3cf2fc81302c00def43126fa13d888e40b0714ab0a0dd84844d9674aac78143d76019a42e8b50cf82418bd0182dd1b6d566f919422e772155017a6c4a81690dfdfd2ba04f456b275ee25a1c101c3bb2dd7223dd2b60a1306fb64f3d77350df43d894b2f1ca42a7463ebf7faf5b5ad9577b47934fee9b374acd837bcd3788c4851094c07c08b79f93517541259337684b5f7969d6e37a315ba0b2215ff9906ecb7611812f6bfdd130b73a5f05ac369823082ee32c08709130b87cb24bcb623ed2f013850fcf5be7953d31be2b5928ddfb4352a6ddd8214a4a196d6b9d17df9a9a44d6828c62a729791cafea9a038cbda0ef824daf1182f8bdf2c1c864a3b98d5873229b9480ed20b4050713b6936002e585fb46d5967aa0ca64905f3b456085eedfa9d247e575a99f24804895436fe42b294b855a1fd2e8f1f9796a5dbea2ab4eaf4c8ef2c580eee97fc82ec1ab1b9c37add1cc1d46d1776a148618806a5bb1509506148909e31ecab91dfb3a346cf13c20172ad099e48d9ceeb6250bb07149507fc2858c760b6eb6103c1b63835ba7ae67b5a3424949e01af5308bf9a4100ab5135beff98f7f5e89d84443c88a979baabc5c04eff9a1a1f4db2ecb0c94cff6b01b97d96d549aa6d991cfd5499b66ac168d276e2012906c455283ddce4326f74b5dfbdb3700f478ae3ab1922140f78610c1c0ba10e3701a65e58717a13d1cc26803f8504f296e4bd71bfaa664ef79660ef83f734ef6d482527beeee32ce1faa8730012642c97e61fe854c4e12e1c2d40a8d8c47aa22da56dc4526d27728c4ea5a6d438aee686c9aa2f27d1f6b1250ca6cf7b00398adb3619fb5bc11304df61ba1a5c8152321a03020186a45bb80e43e5d15c2e4745653db339f5bd8d5a2db978324add97000cd56f19afd3e225a8e56e5ed321cfc99059f214fb4512ce1a19c888f9d87428babb59e85f66c68188a7f2059ced172bf6f414846ce722e191fd76a08b019757158120fe2f73003114b05e65e4ac684c689ef1490b951d3ab1af5f2405dce72d7aabb6b64d5787a1eb4c2f6a286348bcd2d4bc625081375c196842d8a5979c4e05f6cc2fffca52e98c665a91016854349e409b53688034bfae22dfd8235b8ed2b5add192c40272a6b774f88f54fa6480903d2c99ac329a9d36cf59eefb906ef78cd8f48426d65ae624d6404c5b008a456079449452a9f147c7bbc2b796be353fce8e0fda617c108edcccd265e56bbde4fa058dc240b461ba42634f3deeea0532cb18a226a5a5a545e788f3b807c0c501a0edd70e30a14e80a91f58f68edd4be50b268e1bf3a5c6fce5e72c0a9870b286f8a0fbacfb3043cf0ba352a7a65d50ec931a0e486c4eee3d3b3f727019d86772fb7e05c993d0c383a96a14de8ea30882729d842bba245f4d91d91c461433984e0799590c34f8f008e885e7d8f9ea74ec89dd0e14273265b833893a4381f934a711ffd82e708a8ae8d912a815554f20aded40704c5a078c3e7de01d1dea1fffb71522360c9f24f2f2788947004d3ce39990bdef507ebb4509d7e41139963083eabc44ec9a3f36d4b7c41828eaa6223397adf22f00f87b95da44af22c294a827eb53a42aaefaf4224880ae2b944cc55aa0a34481846ccc64267cc317b659fb042d228c4a85bbea62d1e8043019af9492649362628e98367504ba02a5054dd19c247ed48dc954d8913c5131d80a9274f6f395a4fa3614595b3a4447ede4b9434ee2cc60ba3211a686bf0f5b8efe99402e93849311652529c2bedd059ea415751164fd491916d0a89b18b6018a488be34440cc8c89e23e2a73c57d024b12035bb9b46b4d3b234c12bbaa4b3fb0bceeb2c96443d0944090a220a299d41b9dc95f825ea750741626f988280de19ad9b63c61d6d00371efb3c77e3f37bc88a79441a3349834202e4e031f5c566577d34cc1ad4b0e3819b6172b72375a0d4f7924397f074714407e2099929265e3f3d258eccea005b606c9b9232c7d30cb60a5b6d73f85d68d147776fc48cbac42f514f722014b1a9e1ce21d75006c7246a9ced6ec9764b4f0a5cc38f05b5767ab79d9e36459f48d8acae126ac75391b4a99a4cce8cd6d0f95658b97ad431b51c17330121778f6b123cee6561e636b2e8d0375aaaa1ca7708a1bf9df1013c90a7bac0290df2dee647975035a5de926c0e1fbb4575bdd14b52b43369134cbf6d1ef2d3141b5a4e4730c8ba027bdb23ef02d6cce3197597d535bfe52700c0a17ea87853587c94b81f173efecbacc61bfaf75631f49280dbcea5f4c25628c5be84379c40e7e335885e122dcb31fd6e6186dd1f8082fdd08e28bbf0ac837a72738ea6bf4a558dd24bf9bfee45b732ac69b44b13cd1b6dd82ca18b0f6e4f963e9b6c748aea3c087dea6fa7097337a90d8c62455d91624fd83ccebee8f4574b9dfe5217d9e51ae841c55bcb60179daf414dcaac90ea31aee77f7731ce8d907e11545b79b8e8dd6f246ae5ee7e0754ca725e3151a70c5488ade1eaf3ca292ca5189f336e4f11e0197652e4d8aee36e430e5dd3b60e26a7481db81c500c39aa7fc0e2d096a5dc4a66e60fce65993749a709578444990d95684cbd8974b26d9019be5444b81d2505e59432d6ddcff14bc23b606ace2c5ab8aeae700fe176022981e96cf2c74a1a681a628ede789dcf3cad6478a2f2e33cbcc77c382f041ad7ab2ed2200f383410722f3d331627c6a8c457e967471995b2544b428c2e7639e9449094677ccf6753fae31ae345556e74a15469d39092d8cbc170b59837de5399a748559dfe290399ddb1c69ad192ca2efd7238523cbfe0c8e56c5157dff639f955624e6909369b058f74e5c754421a769e848ec3170f7c78bcebc970233f753d3668794fe7a63a72f88e814736a05967a580b171e4e050a536447c7b1ce1998ce76919d0b2fae8a5169b23f29a46fe0085498fe268b481dfbfee04ac6c092d4265f0f4505d6e0bd117ae601cc6882eb40d85427df6322956ed49fa5023b29fb3f5d071363b061d55983e18dcb43b7b68ce61f246e9b17707be90c9959a04a6503d6a1bc8a8d709e397f429232cbf762d1c4b844a3db5de5f5ec51a754d375597eba43ae022c9ed4af7fee6b5c7a87a13b5031fcbd89b48708cb674d7caa70bff8041795bb07db40209994f6b49da3d16e00f73f35df88ef844c738666fae293eaf76852e3a382d28c7a47b52f97ca48992fa78a38a58c8a9ee688843a9046e0dfb8166a3a43cd5031ce02b091aeaf25c5d24988d2361fb2d531fc70954f0a45163007d5bc959662f3c2553cd97a89877d8749ec3160bf56e6b65eaa1683e645ceaf950f9b8efb18fffdbbd27e2c105f2695d4a55750147de538f824c30077e057dc38fba5df6d27ee0e83f3b4abad4a64f10f6e055a86a7d394cb127a291cc4395c2725da2cd3e724c6908140c817f5ada801155ff7a8b8311b48cfb4b0d0e8b2805751d0a04c107a572450818c513e30172bb30c1d780866b5ee9ab57014cf22764cd7996e3ab6e16cf0e1098a7ef1226d6a9d5df4a9dd007a07d6ce4e84cb446d225014d179ecc1a9f4523aa85bbe032c5ef95313cfba089e8b24a2d9956d07de2c57efb806ca41847cf69247e6fe672073d122b2943182c2a326a3f5a19e43accc048117b194243ccc3fa4422bed7e8de99e65b21f1180ecd7d44b752acd4ce9fd32e355a4d9f44f0dee92f2707e337b173eb9300a3ef335cec9dfdd1dc7cf1a6878510cc52e6bde9feb13d4ac48fa8c7020d0fc85fbc413479966621cf47e2df8b335f7aa363b8573860a57be71a27bf50beaec0"));

        EmbeddedChannel channel = new EmbeddedChannel(
                new CipherDecoder("123456")
        );
        channel.writeInbound(b1);
        ByteBuf r1 = channel.readInbound();
        if (r1 == null) {
            log.info("r1 = 0");
        } else {
            log.info("r1 = {}", r1.readableBytes());
        }
        channel.writeInbound(b2);
        ByteBuf r2 = channel.readInbound();
        if (r2 == null) {
            log.info("r2 = 0");
        } else {
            log.info("r2 = {}", r2.readableBytes());
        }
        channel.writeInbound(b3);
        ByteBuf r3 = channel.readInbound();
        if (r3 == null) {
            log.info("r3 = 0");
        } else {
            log.info("r3 = {}", r3.readableBytes());
        }
        channel.writeInbound(b4);
        ByteBuf r4 = channel.readInbound();
        if (r4 == null) {
            log.info("r4 = 0");
        } else {
            log.info("r4 = {}", r4.readableBytes());
        }



//        byte[] bytes = new byte[r1.readableBytes()];
//        r1.getBytes(0, bytes);
//        boolean haveSSHeader = false;
//        if (haveSSHeader) {
//            if (bytes[0] == 0x03) {
//                int size = (bytes[1] & 0xff);
//                int b1 = (bytes[2 + size] & 0xff);
//                int b2 = (bytes[3 + size] & 0xff);
//                byte[] address = new byte[size];
//                System.arraycopy(bytes, 2, address, 0, size);
//                log.info("type = 0x03, size = {}, addr = {}, port = {}", size, new String(address), (b1 << 8) + b2);
//                byte[] info = new byte[bytes.length - 2 - size - 2];
//                System.arraycopy(bytes, 2 + size + 2, info, 0, bytes.length - 2 - size - 2);
//                log.info("{}", HexUtil.encodeHexStr(info));
//            }
//        } else {
//            log.info("{}", HexUtil.encodeHexStr(bytes));
//        }

//        ByteBuf buf3 = channel.readInbound();
//        bytes = new byte[buf3.readableBytes()];
//        buf3.readBytes(bytes);
//        log.info("{}", HexUtil.encodeHexStr(bytes));
    }
}
