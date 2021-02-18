package com.hellozjf.project.shadowsocks.config;

import com.hellozjf.project.shadowsocks.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class SSConfigTest extends BaseTest {

    @Autowired
    private SSConfig ssConfig;

    @Autowired
    private SnowflakeConfig snowflake;

    @Autowired
    private UserPortConfig userPort;

    @Test
    public void test() {
        log.info("ssConfig = {}", ssConfig);
        log.info("snowflake = {}", snowflake);
        log.info("userPort = {}", userPort);
    }
}