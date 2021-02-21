package com.hellozjf.project.shadowsocks.netty;

import com.hellozjf.project.shadowsocks.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Slf4j
public class ProxyTest extends BaseTest {

    @Test
    public void test() {
        while (true) {
            RestTemplate restTemplate = new RestTemplate();
            SimpleClientHttpRequestFactory reqfac = new SimpleClientHttpRequestFactory();
            reqfac.setProxy(new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1081)));
            restTemplate.setRequestFactory(reqfac);

            ResponseEntity<byte[]> forEntity = restTemplate.getForEntity("https://www.youtube.com", byte[].class);
            log.info("s = {}", forEntity.getStatusCodeValue());
        }
    }

    @Test
    public void test2() {
        while (true) {
            RestTemplate restTemplate = new RestTemplate();
            SimpleClientHttpRequestFactory reqfac = new SimpleClientHttpRequestFactory();
            reqfac.setProxy(new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1082)));
            restTemplate.setRequestFactory(reqfac);

            ResponseEntity<byte[]> forEntity = restTemplate.getForEntity("https://www.youtube.com", byte[].class);
            log.info("s = {}", forEntity.getStatusCodeValue());
        }
    }
}
