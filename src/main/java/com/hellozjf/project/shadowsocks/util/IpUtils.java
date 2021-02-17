package com.hellozjf.project.shadowsocks.util;

import cn.hutool.core.net.NetUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;

@Slf4j
public class IpUtils {

    /**
     * 将字节数组形式的IPv4地址转换为我们需要的地址
     * @param bytes
     * @return
     */
    public static String parseIpv4(byte[] bytes) {
        if (bytes == null || bytes.length != 4) {
            return null;
        }
        long ipv4 = (((bytes[0] << 24) + (bytes[1] << 16) + (bytes[2] << 8) + bytes[3]) & 0xffffffff);
        return NetUtil.longToIpv4(ipv4);
    }

    /**
     * 将字节数组形式的IPv6地址转换为我们需要的地址
     * @param bytes
     * @return
     */
    public static String parseIpv6(byte[] bytes) {
        if (bytes == null || bytes.length != 16) {
            return null;
        }
        BigInteger ipv6 = new BigInteger(bytes);
        return NetUtil.bigIntegerToIPv6(ipv6);
    }
}
