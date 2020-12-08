package com.tusi.demo.service.impl;

import com.tusi.demo.annotation.ReadOnly;
import com.tusi.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private DataSource dataSource;

    @Override
    public List<Map<String, Object>> list1() {
        String sql = "SELECT * FROM t_order;";
        JdbcTemplate master = new JdbcTemplate(dataSource);
        List<Map<String, Object>> l1 = master.queryForList(sql);
        return l1;
    }


    @Override
    @ReadOnly("slave")
    public List<Map<String, Object>> list2() {
        String sql = "SELECT * FROM t_order;";
        JdbcTemplate master = new JdbcTemplate(dataSource);
        List<Map<String, Object>> l1 = master.queryForList(sql);
        return l1;
    }
}
