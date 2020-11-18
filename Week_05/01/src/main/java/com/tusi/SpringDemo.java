package com.tusi;

import com.tusi.bean.ISchool;
import com.tusi.bean.Klass;
import com.tusi.bean.Student;
import com.tusi.bean.Teacher;
import com.tusi.config.SpringConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 不同方式实现 Spring Bean 的装配
 */
public class SpringDemo {

    public static void main(String[] args) {
        // Spring 1/2 - xml
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

        Student student1 = (Student) context.getBean("student1");
        System.out.println(student1.toString());

        Student student2 = (Student) context.getBean("student2");
        System.out.println(student2.toString());

        Klass class1 = context.getBean(Klass.class);
        System.out.println(class1);

        ISchool school1 = context.getBean(ISchool.class);
        System.out.println(school1);

        school1.ding();

        class1.dong();

        System.out.println("context.getBeanDefinitionNames() ===>> \n"+ String.join(",", context.getBeanDefinitionNames()));

        // Spring 2.5 - @Component
        ApplicationContext ac1 = new AnnotationConfigApplicationContext(Teacher.class);
        Teacher teacher1 = (Teacher) ac1.getBean("teacher");
        teacher1.setId(1);
        teacher1.setName("teacher1");
        System.out.println(teacher1.toString());

        // Spring 3.0 - @Bean
        ApplicationContext ac2 = new AnnotationConfigApplicationContext(SpringConfig.class);
        Teacher teacher2 = (Teacher) ac2.getBean("teacher");
        System.out.println(teacher2.toString());
    }


}
