package com.example.reciperecommender.controller;

import com.example.reciperecommender.model.Recipe;
import com.example.reciperecommender.model.User;
import com.example.reciperecommender.service.XmlService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class RecommendationController {

    private final XmlService xmlService;

    public RecommendationController(XmlService xmlService) {
        this.xmlService = xmlService;
    }

    @GetMapping("/recommendations")
    public String recommendations(@RequestParam(required = false) String userId, Model model) {
        User user = resolveUser(userId);
        List<Recipe> recipes = xmlService.getRecipesBySkillLevel(user.getCookingSkillLevel());
        populateModel(model, user, recipes, "skill");
        return "recommendations";
    }

    @GetMapping("/recommendations/advanced")
    public String advancedRecommendations(@RequestParam(required = false) String userId, Model model) {
        User user = resolveUser(userId);
        List<Recipe> recipes = xmlService.getRecipesBySkillLevelAndCuisine(
                user.getCookingSkillLevel(), user.getPreferredCuisineType());
        populateModel(model, user, recipes, "advanced");
        return "recommendations";
    }

    private User resolveUser(String userId) {
        User user = null;
        if (userId != null && !userId.isBlank()) {
            user = xmlService.getUserById(userId);
        }
        return user != null ? user : xmlService.getFirstUser();
    }

    private void populateModel(Model model, User user, List<Recipe> recipes, String matchType) {
        model.addAttribute("users", xmlService.getAllUsers());
        model.addAttribute("user", user);
        model.addAttribute("selectedUserId", user != null ? user.getId() : "");
        model.addAttribute("recipes", recipes);
        model.addAttribute("matchType", matchType);
        model.addAttribute("activePage", "recommendations");
    }
}
