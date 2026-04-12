package com.example.reciperecommender.controller;

import com.example.reciperecommender.service.XmlService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
