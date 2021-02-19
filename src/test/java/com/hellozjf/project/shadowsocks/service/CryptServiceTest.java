package com.hellozjf.project.shadowsocks.service;

import com.hellozjf.project.shadowsocks.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class CryptServiceTest extends BaseTest {

    @Autowired
    private CryptService cryptService;

    @Test
    public void test() {
        log.info("cryptService = {}", cryptService);
    }
}