package com.hellozjf.project.shadowsocks.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

    @Bean(name = "bossGroup")
    public EventLoopGroup bossGroup() {
        return new NioEventLoopGroup(1);
    }

    @Bean(name = "workerGroup")
    public EventLoopGroup workerGroup() {
        return new NioEventLoopGroup(2);
    }

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }
}
