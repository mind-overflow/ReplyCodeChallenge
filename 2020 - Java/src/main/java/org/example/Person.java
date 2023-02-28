package org.example;


import java.util.List;

class Person {
    final Job job;
    final String company;
    final int bonus;

    Point position;

    List<String> skills;
    public Person(Job job, String company, List<String> skills, int bonus, Point position)
    {
        this.job = job;
        this.company = company;
        this.skills = skills;
        this.position = position;
        this.bonus = bonus;
    }
}