package com.example.reciperecommender.controller;

import com.example.reciperecommender.model.Recipe;
import com.example.reciperecommender.model.RecipeConstants;
import com.example.reciperecommender.service.XmlService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RecipeController {

    private final XmlService xmlService;

    public RecipeController(XmlService xmlService) {
        this.xmlService = xmlService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("recipeCount", xmlService.getAllRecipes().size());
        model.addAttribute("userCount", xmlService.getAllUsers().size());
        model.addAttribute("cuisineCount", RecipeConstants.CUISINE_TYPES.size());
        model.addAttribute("activePage", "home");
        return "home";
    }

    @GetMapping("/recipes")
    public String listRecipes(Model model) {
        model.addAttribute("recipes", xmlService.getAllRecipes());
        model.addAttribute("cuisineTypes", RecipeConstants.CUISINE_TYPES);
        model.addAttribute("activePage", "recipes");
        return "recipes";
    }

    @GetMapping("/recipes/{id}")
    public String recipeDetail(@PathVariable String id, Model model,
                               RedirectAttributes redirectAttributes) {
        Recipe recipe = xmlService.getRecipeById(id);
        if (recipe == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Recipe not found.");
            return "redirect:/recipes";
        }
        List<Recipe> similarRecipes = xmlService.getRecipesBySkillLevel(recipe.getDifficulty())
                .stream()
                .filter(r -> !r.getId().equals(id))
                .limit(3)
                .collect(Collectors.toList());
        model.addAttribute("recipe", recipe);
        model.addAttribute("similarRecipes", similarRecipes);
        model.addAttribute("activePage", "recipes");
        return "recipe-detail";
    }

    @GetMapping("/recipes/filter")
    public String filterRecipes(
            @RequestParam(defaultValue = "") String cuisine,
            Model model) {

        if (!cuisine.isEmpty() && !RecipeConstants.CUISINE_TYPES.contains(cuisine)) {
            return "redirect:/recipes/filter";
        }

        List<Recipe> recipes = cuisine.isEmpty()
                ? xmlService.getAllRecipes()
                : xmlService.getRecipesByCuisineType(cuisine);

        model.addAttribute("recipes", recipes);
        model.addAttribute("cuisineTypes", RecipeConstants.CUISINE_TYPES);
        model.addAttribute("selectedCuisine", cuisine);
        model.addAttribute("activePage", "filter");
        return "filter-recipes";
    }

    @GetMapping("/recipes/add")
    public String showAddRecipeForm(Model model) {
        model.addAttribute("cuisineTypes", RecipeConstants.CUISINE_TYPES);
        model.addAttribute("difficultyLevels", RecipeConstants.DIFFICULTY_LEVELS);
        model.addAttribute("inputTitle", "");
        model.addAttribute("inputCuisineType1", "");
        model.addAttribute("inputCuisineType2", "");
        model.addAttribute("inputDifficulty", "");
        model.addAttribute("activePage", "add-recipe");
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

        String trimmedTitle = title.trim();
        if (trimmedTitle.isEmpty()) {
            titleError = "Title is required.";
        } else if (trimmedTitle.length() < 2 || trimmedTitle.length() > 100) {
            titleError = "Title must be between 2 and 100 characters.";
        }

        if (cuisineType1.isEmpty() || cuisineType2.isEmpty()) {
            cuisineError = "Both cuisine types must be selected.";
        } else if (cuisineType1.equals(cuisineType2)) {
            cuisineError = "The two cuisine types must be different.";
        } else if (!RecipeConstants.CUISINE_TYPES.contains(cuisineType1)
                || !RecipeConstants.CUISINE_TYPES.contains(cuisineType2)) {
            cuisineError = "Invalid cuisine type selected.";
        }

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
            model.addAttribute("activePage", "add-recipe");
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
