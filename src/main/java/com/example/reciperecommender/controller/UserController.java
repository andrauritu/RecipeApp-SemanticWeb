package com.example.reciperecommender.controller;

import com.example.reciperecommender.model.RecipeConstants;
import com.example.reciperecommender.model.User;
import com.example.reciperecommender.service.XmlService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private static final java.util.regex.Pattern NAME_PATTERN =
            java.util.regex.Pattern.compile("^[\\p{L} ]+$");

    private final XmlService xmlService;

    public UserController(XmlService xmlService) {
        this.xmlService = xmlService;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", xmlService.getAllUsers());
        return "users";
    }

    @GetMapping("/users/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("skillLevels", RecipeConstants.SKILL_LEVELS);
        model.addAttribute("cuisineTypes", RecipeConstants.CUISINE_TYPES);
        model.addAttribute("inputName", "");
        model.addAttribute("inputSurname", "");
        model.addAttribute("inputSkillLevel", "");
        model.addAttribute("inputCuisineType", "");
        return "add-user";
    }

    @PostMapping("/users/add")
    public String handleAddUser(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String surname,
            @RequestParam(defaultValue = "") String skillLevel,
            @RequestParam(defaultValue = "") String cuisineType,
            Model model,
            RedirectAttributes redirectAttributes) {

        String nameError = null;
        String surnameError = null;
        String skillLevelError = null;
        String cuisineTypeError = null;

        // Validate name
        String trimmedName = name.trim();
        if (trimmedName.isEmpty()) {
            nameError = "Name is required.";
        } else if (trimmedName.length() < 2 || trimmedName.length() > 50) {
            nameError = "Name must be between 2 and 50 characters.";
        } else if (!NAME_PATTERN.matcher(trimmedName).matches()) {
            nameError = "Name may only contain letters and spaces.";
        }

        // Validate surname
        String trimmedSurname = surname.trim();
        if (trimmedSurname.isEmpty()) {
            surnameError = "Surname is required.";
        } else if (trimmedSurname.length() < 2 || trimmedSurname.length() > 50) {
            surnameError = "Surname must be between 2 and 50 characters.";
        } else if (!NAME_PATTERN.matcher(trimmedSurname).matches()) {
            surnameError = "Surname may only contain letters and spaces.";
        }

        // Validate skill level
        if (skillLevel.isEmpty()) {
            skillLevelError = "Cooking skill level is required.";
        } else if (!RecipeConstants.SKILL_LEVELS.contains(skillLevel)) {
            skillLevelError = "Invalid cooking skill level selected.";
        }

        // Validate cuisine type
        if (cuisineType.isEmpty()) {
            cuisineTypeError = "Preferred cuisine type is required.";
        } else if (!RecipeConstants.CUISINE_TYPES.contains(cuisineType)) {
            cuisineTypeError = "Invalid cuisine type selected.";
        }

        boolean hasErrors = nameError != null || surnameError != null
                || skillLevelError != null || cuisineTypeError != null;

        if (hasErrors) {
            model.addAttribute("skillLevels", RecipeConstants.SKILL_LEVELS);
            model.addAttribute("cuisineTypes", RecipeConstants.CUISINE_TYPES);
            model.addAttribute("nameError", nameError);
            model.addAttribute("surnameError", surnameError);
            model.addAttribute("skillLevelError", skillLevelError);
            model.addAttribute("cuisineTypeError", cuisineTypeError);
            model.addAttribute("inputName", name);
            model.addAttribute("inputSurname", surname);
            model.addAttribute("inputSkillLevel", skillLevel);
            model.addAttribute("inputCuisineType", cuisineType);
            return "add-user";
        }

        User user = new User(null, trimmedName, trimmedSurname, skillLevel, cuisineType);
        xmlService.addUser(user);

        redirectAttributes.addFlashAttribute("successMessage", "User added successfully!");
        return "redirect:/users";
    }
}
