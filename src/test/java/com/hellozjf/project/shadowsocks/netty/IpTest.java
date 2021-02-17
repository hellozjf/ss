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
}
