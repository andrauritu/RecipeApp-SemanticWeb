package com.example.reciperecommender.controller;

import com.example.reciperecommender.model.User;
import com.example.reciperecommender.service.XmlService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class RecipeXslController {

    private final XmlService xmlService;

    public RecipeXslController(XmlService xmlService) {
        this.xmlService = xmlService;
    }

    @GetMapping("/recipes/styled")
    public String styledRecipes(
            @RequestParam(required = false) String userId,
            Model model) {

        List<User> users = xmlService.getAllUsers();

        User selectedUser = null;
        if (userId != null && !userId.isBlank()) {
            selectedUser = xmlService.getUserById(userId);
        }
        if (selectedUser == null) {
            selectedUser = xmlService.getFirstUser();
        }

        String transformedHtml = xmlService.transformRecipesWithXsl(
                selectedUser != null ? selectedUser.getCookingSkillLevel() : "");

        model.addAttribute("users", users);
        model.addAttribute("selectedUser", selectedUser);
        model.addAttribute("selectedUserId", selectedUser != null ? selectedUser.getId() : "");
        model.addAttribute("transformedHtml", transformedHtml);
        model.addAttribute("activePage", "styled");
        return "styled-recipes";
    }
}
