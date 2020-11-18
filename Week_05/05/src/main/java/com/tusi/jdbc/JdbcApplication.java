package com.tusi.jdbc;

import com.tusi.jdbc.entity.JdbcEntity;
import com.tusi.jdbc.example.DataSourceExample;
import com.tusi.jdbc.example.JdbcNativeExample;
import com.tusi.jdbc.example.TransactionExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@SpringBootApplication
@EnableTransactionManagement
@Slf4j
public class JdbcApplication implements CommandLineRunner {
	@Autowired
	private JdbcEntity jdbcEntity;
	@Autowired
	private TransactionExample transactionExample;
	@Autowired
	private DataSourceExample dataSourceExample;
	@Autowired
	private DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(JdbcApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		JdbcNativeExample jdbcNative = new JdbcNativeExample();
		jdbcNative.exec(jdbcEntity);

		transactionExample.exec(jdbcEntity);

		log.info(dataSource.toString());
		dataSourceExample.exec(dataSource);
	}

}
