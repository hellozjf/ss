package com.hellozjf.project.shadowsocks.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ss")
public class SSConfig {
    private SnowflakeConfig snowflake;
    private UserPortConfig userPort;
}
