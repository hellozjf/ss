package com.hellozjf.project.shadowsocks.netty;

import cn.hutool.core.net.NetUtil;
import com.hellozjf.project.shadowsocks.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;

@Slf4j
public class IpTest extends BaseTest {

    @Test
    public void test() {
        byte[] bytes = new byte[] {(byte) 192, (byte) 168, (byte) 31, (byte) 12};
        String ip = (bytes[0] & 0xff) + "." + (bytes[1] & 0xff) + "." + (bytes[2] & 0xff) + "." + (bytes[3] & 0xff);
        log.info("ip = {}", ip);
    }

    @Test
    public void testIpv4() {
        LinkedHashSet<String> ipv4s = NetUtil.localIpv4s();
        for (String ipv4 : ipv4s) {
            log.info("{}", ipv4);
        }
        long l = NetUtil.ipv4ToLong("192.168.31.12");
    }

    @Test
    public void size() {
        log.info("size = {}", "030d7777772e62616964752e636f6d01bb1603010200010001fc03031584bc778be3f35307c8d67ba202d9884a67b3c912cf5ae2f3eaa2831f950375205b39ad641d553a381b5a5253fc39d379cebfb0f37fb590e61b187c9c83ee6b0800207a7a130113021303c02bc02fc02cc030cca9cca8c013c014009c009d002f0035010001937a7a000000000012001000000d7777772e62616964752e636f6d00170000ff01000100000a000a00081a1a001d00170018000b00020100002300a0705b30e5fa155e00e4e55c465624cec48cb231c37cf1e27b7273e68cdc18a1a36a3f4bc699f46e4fdba68fe2e03824d2075b868db45d1587271620108e77a702d40083ec2f0f5779d8665c95f7295c0c84ca339b7093491b7f32959ac61052fe807a48f4d398b4b71a4bb061698b8651ad198aa3577e31e37d24eb4a68dc725325c6f7309e028bf477229fbcb8427d13fc512c28a95499e82f9a3e38bdbe13060010000e000c02683208687474702f312e31000500050100000000000d0012001004030804040105030805050108060601001200000033002b00291a1a000100001d0020555c6b09140236525dbac8afd9210f48374cbea571e577416e9e973df3d9bf68002d00020101002b000b0a8a8a0304030303020301001b0003020002fafa0001000015002f000000000000000000000000000000000000000000000000000000000000".length());
    }
}
