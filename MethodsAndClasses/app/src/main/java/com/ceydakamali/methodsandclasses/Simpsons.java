package com.ceydakamali.methodsandclasses;

public class Simpsons {
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    private String job;

    public Simpsons(String name, int age, String job) {
        this.name = name;
        this.age = age;
        this.job = job;
    }
}
