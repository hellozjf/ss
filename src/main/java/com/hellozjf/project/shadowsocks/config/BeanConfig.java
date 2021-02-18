package com.hellozjf.project.shadowsocks.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * bean
 */
@Configuration
public class BeanConfig {

    @Autowired
    private SnowflakeConfig snowflakeConfig;

    @Bean
    public Snowflake snowflake() {
        return IdUtil.getSnowflake(snowflakeConfig.getWorkerId(), snowflakeConfig.getDatacenterId());
    }
}
