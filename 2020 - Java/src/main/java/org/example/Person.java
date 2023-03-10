package org.example;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
class Person {
    @Getter
    private final Job job;
    @Getter
    private final String company;
    @Getter
    private final int bonus;

    @Getter @Setter
    private Point position;

    @Getter
    private final List<String> skills;
    public Person(Job job, String company, int bonus, List<String> skills)
    {
        this.job = job;
        this.company = company;
        this.skills = skills;
        this.bonus = bonus;
        this.position = null;
    }

    public Person(Job job, String company, int bonus)
    {
        this.job = job;
        this.company = company;
        this.skills = null;
        this.bonus = bonus;
        this.position = null;
    }
}