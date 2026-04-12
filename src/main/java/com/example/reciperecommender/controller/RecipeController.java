package com.example.reciperecommender.controller;

import com.example.reciperecommender.model.Recipe;
import com.example.reciperecommender.model.RecipeConstants;
import com.example.reciperecommender.service.XmlService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RecipeController {

    private final XmlService xmlService;

    public RecipeController(XmlService xmlService) {
        this.xmlService = xmlService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/recipes";
    }

    @GetMapping("/recipes")
    public String listRecipes(Model model) {
        model.addAttribute("recipes", xmlService.getAllRecipes());
        model.addAttribute("users", xmlService.getAllUsers());
        return "recipes";
    }

    @GetMapping("/recipes/add")
    public String showAddRecipeForm(Model model) {
        model.addAttribute("cuisineTypes", RecipeConstants.CUISINE_TYPES);
        model.addAttribute("difficultyLevels", RecipeConstants.DIFFICULTY_LEVELS);
        // Pre-populate empty values so the template never needs to handle null
        model.addAttribute("inputTitle", "");
        model.addAttribute("inputCuisineType1", "");
        model.addAttribute("inputCuisineType2", "");
        model.addAttribute("inputDifficulty", "");
        return "add-recipe";
    }

    @PostMapping("/recipes/add")
    public String handleAddRecipe(
            @RequestParam(defaultValue = "") String title,
            @RequestParam(defaultValue = "") String cuisineType1,
            @RequestParam(defaultValue = "") String cuisineType2,
            @RequestParam(defaultValue = "") String difficulty,
            Model model,
            RedirectAttributes redirectAttributes) {

        String titleError = null;
        String cuisineError = null;
        String difficultyError = null;

        // Validate title
        String trimmedTitle = title.trim();
        if (trimmedTitle.isEmpty()) {
            titleError = "Title is required.";
        } else if (trimmedTitle.length() < 2 || trimmedTitle.length() > 100) {
            titleError = "Title must be between 2 and 100 characters.";
        }

        // Validate cuisine types
        if (cuisineType1.isEmpty() || cuisineType2.isEmpty()) {
            cuisineError = "Both cuisine types must be selected.";
        } else if (cuisineType1.equals(cuisineType2)) {
            cuisineError = "The two cuisine types must be different.";
        } else if (!RecipeConstants.CUISINE_TYPES.contains(cuisineType1)
                || !RecipeConstants.CUISINE_TYPES.contains(cuisineType2)) {
            cuisineError = "Invalid cuisine type selected.";
        }

        // Validate difficulty
        if (difficulty.isEmpty()) {
            difficultyError = "Difficulty is required.";
        } else if (!RecipeConstants.DIFFICULTY_LEVELS.contains(difficulty)) {
            difficultyError = "Invalid difficulty level selected.";
        }

        boolean hasErrors = titleError != null || cuisineError != null || difficultyError != null;

        if (hasErrors) {
            model.addAttribute("cuisineTypes", RecipeConstants.CUISINE_TYPES);
            model.addAttribute("difficultyLevels", RecipeConstants.DIFFICULTY_LEVELS);
            model.addAttribute("titleError", titleError);
            model.addAttribute("cuisineError", cuisineError);
            model.addAttribute("difficultyError", difficultyError);
            model.addAttribute("inputTitle", title);
            model.addAttribute("inputCuisineType1", cuisineType1);
            model.addAttribute("inputCuisineType2", cuisineType2);
            model.addAttribute("inputDifficulty", difficulty);
            return "add-recipe";
        }

        List<String> selectedCuisines = new ArrayList<>();
        selectedCuisines.add(cuisineType1);
        selectedCuisines.add(cuisineType2);

        Recipe recipe = new Recipe(null, trimmedTitle, selectedCuisines, difficulty);
        xmlService.addRecipe(recipe);

        redirectAttributes.addFlashAttribute("successMessage", "Recipe added successfully!");
        return "redirect:/recipes";
    }
}
