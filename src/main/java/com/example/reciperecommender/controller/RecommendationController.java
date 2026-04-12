package com.example.reciperecommender.controller;

import com.example.reciperecommender.model.Recipe;
import com.example.reciperecommender.model.User;
import com.example.reciperecommender.service.XmlService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RecommendationController {

    private final XmlService xmlService;

    public RecommendationController(XmlService xmlService) {
        this.xmlService = xmlService;
    }

    @GetMapping("/recommendations")
    public String recommendations(Model model) {
        User user = xmlService.getFirstUser();
        List<Recipe> recipes = xmlService.getRecipesBySkillLevel(user.getCookingSkillLevel());
        model.addAttribute("user", user);
        model.addAttribute("recipes", recipes);
        model.addAttribute("matchType", "skill");
        model.addAttribute("activePage", "recommendations");
        return "recommendations";
    }

    @GetMapping("/recommendations/advanced")
    public String advancedRecommendations(Model model) {
        User user = xmlService.getFirstUser();
        List<Recipe> recipes = xmlService.getRecipesBySkillLevelAndCuisine(
                user.getCookingSkillLevel(), user.getPreferredCuisineType());
        model.addAttribute("user", user);
        model.addAttribute("recipes", recipes);
        model.addAttribute("matchType", "advanced");
        model.addAttribute("activePage", "recommendations");
        return "recommendations";
    }
}
