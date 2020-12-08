package com.tusi.demo.controller;

import com.tusi.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public String list1() {
        return orderService.list().toString();
    }

    @GetMapping("/insert")
    public int insert() {
        return orderService.insertOrder();
    }
}
