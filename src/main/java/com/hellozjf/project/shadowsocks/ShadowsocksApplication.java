package com.hellozjf.project.shadowsocks;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hellozjf.project.shadowsocks.dao.mapper")
public class ShadowsocksApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShadowsocksApplication.class, args);
	}

}
