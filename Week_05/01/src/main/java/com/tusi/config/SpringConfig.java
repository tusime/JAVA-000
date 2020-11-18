package com.tusi.config;

import com.tusi.bean.Teacher;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

@Configurable
public class SpringConfig {
    @Bean("teacher")
    public Teacher getTeacher() {
        return new Teacher(2,"teacher2");
    }
}
