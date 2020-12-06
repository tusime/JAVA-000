package com.tusi.demo.controller;

import com.tusi.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 主库测试
    @GetMapping("/list1")
    public String list1() {
        return orderService.list1().toString();
    }

    // 从库测试
    @GetMapping("/list2")
    public String list2() {
        return orderService.list2().toString();
    }

}
