package com.hellozjf.project.shadowsocks.netty;

import com.hellozjf.project.shadowsocks.BaseTest;
import com.hellozjf.project.shadowsocks.service.NettyService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerTest extends BaseTest {

    @Autowired
    private NettyService nettyService;

    @Test
    public void listen() throws InterruptedException {
        Channel channel = nettyService.createPort(8388, "123456", "aes-256-gcm");
        while (true) {
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
