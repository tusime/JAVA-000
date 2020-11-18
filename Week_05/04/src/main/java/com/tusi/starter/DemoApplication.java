package com.tusi.starter;

import com.tusi.starter.bean.Klass;
import com.tusi.starter.bean.School;
import com.tusi.starter.bean.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Student/Klass/School 实现自动配置和 Starter
 */
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	@Autowired
	private Student student;
	@Autowired
	private Klass klass;
	@Autowired
	private School school;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println(student.toString());
		System.out.println(klass.toString());
		System.out.println(school.toString());
	}
}
