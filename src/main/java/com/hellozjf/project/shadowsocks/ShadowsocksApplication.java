package com.hellozjf.project.shadowsocks;

import com.hellozjf.project.shadowsocks.service.NettyService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@MapperScan("com.hellozjf.project.shadowsocks.dao.mapper")
public class ShadowsocksApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShadowsocksApplication.class, args);
	}

	@Bean
	@Profile("!unittest")
	public CommandLineRunner commandLineRunner(NettyService nettyService) {
		return args -> {
			// 程序启动的时候，读取数据库里面的用户信息，然后启动shadowsocks服务
			nettyService.init();
		};
	}
}
