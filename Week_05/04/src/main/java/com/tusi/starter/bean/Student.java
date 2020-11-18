package com.tusi.starter.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ConfigurationProperties("student")
public class Student {
    private int id;
    private String name;

    public void init(){
        System.out.println("init Student...........");
    }

}
