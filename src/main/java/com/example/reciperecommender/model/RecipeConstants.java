package com.example.reciperecommender.model;

import java.util.List;

public class RecipeConstants {

    public static final List<String> CUISINE_TYPES = List.of(
            "Italian", "Asian", "French", "Mexican",
            "Mediterranean", "Indian", "American", "Japanese"
    );

    public static final List<String> DIFFICULTY_LEVELS = List.of(
            "Beginner", "Intermediate", "Advanced"
    );

    public static final List<String> SKILL_LEVELS = List.of(
            "Beginner", "Intermediate", "Advanced"
    );

    private RecipeConstants() {}
}
