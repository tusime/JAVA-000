package com.tusi.bean;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

public class School implements ISchool{

    // spring 注入
    @Autowired(required = true)
    Klass class1;
    // j2ee 注入
    @Resource(name = "student1")
    Student student1;

    @Override
    public void ding() {
        System.out.println("Class1 have " + this.class1.getStudents().size() + " students and one is " + this.student1);
    }

    @Override
    public String toString() {
        return "School{" +
                "class1=" + class1 +
                ", student1=" + student1 +
                '}';
    }
}
