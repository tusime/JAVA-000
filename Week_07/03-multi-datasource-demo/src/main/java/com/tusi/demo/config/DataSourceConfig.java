package com.tusi.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class DataSourceConfig {

    @Primary
    @Bean(name = "dataSource")
    public DataSource dataSource() {
        DataSource masterDataSource = masterDataSource();
        DataSource slave1DataSource = slave1DataSource();
        DataSource slave2DataSource = slave2DataSource();

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("master", masterDataSource);
        targetDataSources.put("slave1", slave1DataSource);
        targetDataSources.put("slave2", slave2DataSource);

        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSources);
        // 默认数据源
        dataSource.setDefaultTargetDataSource(masterDataSource);
        return dataSource;
    }

    /* master */
    @Bean
    @ConfigurationProperties("master.datasource")
    public DataSourceProperties masterDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("masterDataSource")
    public DataSource masterDataSource() {
        DataSourceProperties dataSourceProperties = masterDataSourceProperties();
        log.info("master datasource: {}", dataSourceProperties.getUrl());
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    @Resource
    public PlatformTransactionManager masterTxManager(DataSource masterDataSource) {
        return new DataSourceTransactionManager(masterDataSource);
    }

    /* slave1 */
    @Bean
    @ConfigurationProperties("slave1.datasource")
    public DataSourceProperties slave1DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("slave1DataSource")
    public DataSource slave1DataSource() {
        DataSourceProperties dataSourceProperties = slave1DataSourceProperties();
        log.info("slave1 datasource: {}", dataSourceProperties.getUrl());
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    @Resource
    public PlatformTransactionManager slave1TxManager(DataSource slave1DataSource) {
        return new DataSourceTransactionManager(slave1DataSource);
    }

    /* slave2 */
    @Bean
    @ConfigurationProperties("slave2.datasource")
    public DataSourceProperties slave2DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("slave2DataSource")
    public DataSource slave2DataSource() {
        DataSourceProperties dataSourceProperties = slave2DataSourceProperties();
        log.info("slave2 datasource: {}", dataSourceProperties.getUrl());
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    @Resource
    public PlatformTransactionManager slave2TxManager(DataSource slave2DataSource) {
        return new DataSourceTransactionManager(slave2DataSource);
    }
}
