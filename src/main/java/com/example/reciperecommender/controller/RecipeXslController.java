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

        // Select the user matching the requested id, falling back to the first user
        User selectedUser = users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(users.isEmpty() ? null : users.get(0));

        String transformedHtml = xmlService.transformRecipesWithXsl(
                selectedUser != null ? selectedUser.getCookingSkillLevel() : "");

        model.addAttribute("users", users);
        model.addAttribute("selectedUser", selectedUser);
        model.addAttribute("selectedUserId", selectedUser != null ? selectedUser.getId() : "");
        model.addAttribute("transformedHtml", transformedHtml);
        return "styled-recipes";
    }
}
