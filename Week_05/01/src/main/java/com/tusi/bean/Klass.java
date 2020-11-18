package com.tusi.bean;

import java.util.List;

public class Klass {
    List<Student> students;

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Student> getStudents() {
        return students;
    }

    public Klass(List<Student> students) {
        this.students = students;
    }

    public void dong(){
        System.out.println(this.getStudents());
    }

    @Override
    public String toString() {
        return "Klass{" +
                "students=" + students +
                '}';
    }
}
