package com.tusi.demo.service;

import java.util.List;
import java.util.Map;

public interface OrderService {
    List<Map<String, Object>> list();
    int insertOrder();
}
