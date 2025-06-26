package com.quitsmoking.platform.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Coach {

    @Id
    private Long id;
    private String name;
    private String specialization;
    private int experienceYears;

    // Constructors
    public Coach() {
    }

    public Coach(Long id, String name, String specialization, int experienceYears) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.experienceYears = experienceYears;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }
}
