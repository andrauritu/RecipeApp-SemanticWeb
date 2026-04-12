package com.example.reciperecommender.model;

public class User {

    private String id;
    private String name;
    private String surname;
    private String cookingSkillLevel;
    private String preferredCuisineType;

    public User() {}

    public User(String id, String name, String surname, String cookingSkillLevel, String preferredCuisineType) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.cookingSkillLevel = cookingSkillLevel;
        this.preferredCuisineType = preferredCuisineType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCookingSkillLevel() {
        return cookingSkillLevel;
    }

    public void setCookingSkillLevel(String cookingSkillLevel) {
        this.cookingSkillLevel = cookingSkillLevel;
    }

    public String getPreferredCuisineType() {
        return preferredCuisineType;
    }

    public void setPreferredCuisineType(String preferredCuisineType) {
        this.preferredCuisineType = preferredCuisineType;
    }
}
