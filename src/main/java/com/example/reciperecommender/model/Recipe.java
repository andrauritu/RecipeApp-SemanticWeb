package com.example.reciperecommender.model;

import java.util.List;

public class Recipe {

    private String id;
    private String title;
    private List<String> cuisineTypes;
    private String difficulty;

    public Recipe() {}

    public Recipe(String id, String title, List<String> cuisineTypes, String difficulty) {
        this.id = id;
        this.title = title;
        this.cuisineTypes = cuisineTypes;
        this.difficulty = difficulty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getCuisineTypes() {
        return cuisineTypes;
    }

    public void setCuisineTypes(List<String> cuisineTypes) {
        this.cuisineTypes = cuisineTypes;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
