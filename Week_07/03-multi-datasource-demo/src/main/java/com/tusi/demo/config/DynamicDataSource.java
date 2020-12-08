package com.tusi.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 数据源路由
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        // 从共享线程中获取数据源名称。负载均衡，可以在这里进行从库的key轮询
        return DynamicDataSourceHolder.getDataSource();
    }
}
