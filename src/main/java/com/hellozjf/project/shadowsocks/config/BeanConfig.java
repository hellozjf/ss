package com.hellozjf.project.shadowsocks.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * bean
 */
@Slf4j
@Configuration
public class BeanConfig {

    @Autowired
    private SnowflakeConfig snowflakeConfig;

    @Bean
    public Snowflake snowflake() {
        return IdUtil.getSnowflake(snowflakeConfig.getWorkerId(), snowflakeConfig.getDatacenterId());
    }

    @Bean(name = "md5MessageDigest")
    public MessageDigest md5MessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error("e = {}", e.getMessage());
            return null;
        }
    }
}
