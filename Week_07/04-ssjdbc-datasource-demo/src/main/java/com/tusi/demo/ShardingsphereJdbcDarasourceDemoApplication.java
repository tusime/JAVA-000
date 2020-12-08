package com.tusi.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
@Slf4j
public class ShardingsphereJdbcDarasourceDemoApplication implements CommandLineRunner {

	@Autowired
	private DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(ShardingsphereJdbcDarasourceDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			log.info("默认数据库 dataSource={}", connection.getMetaData().getURL());
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
}
