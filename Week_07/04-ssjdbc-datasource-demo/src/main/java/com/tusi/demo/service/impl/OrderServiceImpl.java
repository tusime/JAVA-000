package com.tusi.demo.service.impl;

import com.tusi.demo.dao.OrderDao;
import com.tusi.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Override
    public List<Map<String, Object>> list() {
        return orderDao.list();
    }

    @Override
    public int insertOrder() {
        return orderDao.insertOrder();
    }
}
