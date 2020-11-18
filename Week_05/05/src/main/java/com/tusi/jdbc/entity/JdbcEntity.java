package com.tusi.jdbc.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("jdbc")
public class JdbcEntity {
    private String driverclass;
    private String url;
    private String username;
    private String password;
}
