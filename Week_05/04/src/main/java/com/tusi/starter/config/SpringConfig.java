package com.tusi.starter.config;

import com.tusi.starter.bean.Klass;
import com.tusi.starter.bean.School;
import com.tusi.starter.bean.Student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SpringConfig {
    @Autowired
    private Klass klass;

    @Bean
    public Student getStudent() {
        Student student = new Student();
        return student;
    }

    @Bean
    public School getSchool() {
        School school = new School();

        Klass klass2 = new Klass();
        List<Student> ls = new ArrayList<>();
        ls.add(getStudent());
        klass2.setStudents(ls);

        List<Klass> lk = new ArrayList<>();
        lk.add(klass);
        lk.add(klass2);
        school.setKlasses(lk);
        return school;
    }
}
