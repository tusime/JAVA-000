package com.tusi.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import java.util.List;
import java.util.Map;

/**
 * 参考 https://yq.aliyun.com/articles/188540
 */

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		JdbcTemplateAutoConfiguration.class})
@Slf4j
public class MultiDatasourceDemoApplication implements CommandLineRunner {

	@Autowired
	@Qualifier("masterDataSource")
	private DataSource masterDataSource;
	@Autowired
	@Qualifier("slave1DataSource")
	private DataSource slave1Datasource;
	@Autowired
	@Qualifier("slave2DataSource")
	private DataSource slave2Datasource;

	public static void main(String[] args) {
		SpringApplication.run(MultiDatasourceDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// 启动时查看各个库的数据情况
		String sql = "SELECT * FROM t_order;";
		JdbcTemplate master = new JdbcTemplate(masterDataSource);
		List<Map<String, Object>> l1 = master.queryForList(sql);
		log.info("masterDataSource list ==> {}", l1.toString());

		JdbcTemplate slave1 = new JdbcTemplate(slave1Datasource);
		List<Map<String, Object>> l2 = slave1.queryForList(sql);
		log.info("slave1Datasource list ==> {}", l2.toString());

		JdbcTemplate slave2 = new JdbcTemplate(slave2Datasource);
		List<Map<String, Object>> l3 = slave2.queryForList(sql);
		log.info("slave2Datasource list ==> {}", l3.toString());
	}
}
