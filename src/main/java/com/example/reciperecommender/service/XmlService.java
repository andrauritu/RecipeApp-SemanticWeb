package com.example.reciperecommender.service;

import com.example.reciperecommender.model.Recipe;
import com.example.reciperecommender.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@DependsOn("recipeScraperService")
public class XmlService {

    @Value("${xml.data.path}")
    private String xmlDataPath;

    private Document recipesDocument;
    private Document usersDocument;

    @PostConstruct
    public void init() {
        reloadRecipes();
        reloadUsers();
    }

    public void reloadRecipes() {
        recipesDocument = loadDocument(new File(xmlDataPath, "recipes.xml"));
    }

    public void reloadUsers() {
        usersDocument = loadDocument(new File(xmlDataPath, "users.xml"));
    }

    private Document loadDocument(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load XML file: " + file.getAbsolutePath(), e);
        }
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        NodeList recipeNodes = recipesDocument.getElementsByTagName("recipe");
        for (int i = 0; i < recipeNodes.getLength(); i++) {
            Element recipeEl = (Element) recipeNodes.item(i);
            String id = recipeEl.getAttribute("id");
            String title = recipeEl.getElementsByTagName("title").item(0).getTextContent();

            List<String> cuisineTypes = new ArrayList<>();
            NodeList cuisineNodes = recipeEl.getElementsByTagName("cuisineType");
            for (int j = 0; j < cuisineNodes.getLength(); j++) {
                cuisineTypes.add(cuisineNodes.item(j).getTextContent().trim());
            }

            String difficulty = recipeEl.getElementsByTagName("difficulty").item(0).getTextContent().trim();

            recipes.add(new Recipe(id, title, cuisineTypes, difficulty));
        }
        return recipes;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        NodeList userNodes = usersDocument.getElementsByTagName("user");
        for (int i = 0; i < userNodes.getLength(); i++) {
            Element userEl = (Element) userNodes.item(i);
            String id = userEl.getAttribute("id");
            String name = userEl.getElementsByTagName("name").item(0).getTextContent().trim();
            String surname = userEl.getElementsByTagName("surname").item(0).getTextContent().trim();
            String cookingSkillLevel = userEl.getElementsByTagName("cookingSkillLevel").item(0).getTextContent().trim();
            String preferredCuisineType = userEl.getElementsByTagName("preferredCuisineType").item(0).getTextContent().trim();

            users.add(new User(id, name, surname, cookingSkillLevel, preferredCuisineType));
        }
        return users;
    }

    public void addRecipe(Recipe recipe) {
        // Generate new id = max existing id + 1
        NodeList recipeNodes = recipesDocument.getElementsByTagName("recipe");
        int maxId = 0;
        for (int i = 0; i < recipeNodes.getLength(); i++) {
            Element el = (Element) recipeNodes.item(i);
            try {
                int id = Integer.parseInt(el.getAttribute("id"));
                if (id > maxId) maxId = id;
            } catch (NumberFormatException ignored) {}
        }
        String newId = String.valueOf(maxId + 1);
        recipe.setId(newId);

        // Build new <recipe> DOM element
        Element recipeEl = recipesDocument.createElement("recipe");
        recipeEl.setAttribute("id", newId);

        Element titleEl = recipesDocument.createElement("title");
        titleEl.setTextContent(recipe.getTitle());
        recipeEl.appendChild(titleEl);

        Element cuisineTypesEl = recipesDocument.createElement("cuisineTypes");
        for (String ct : recipe.getCuisineTypes()) {
            Element cuisineTypeEl = recipesDocument.createElement("cuisineType");
            cuisineTypeEl.setAttribute("value", ct);
            cuisineTypeEl.setTextContent(ct);
            cuisineTypesEl.appendChild(cuisineTypeEl);
        }
        recipeEl.appendChild(cuisineTypesEl);

        Element difficultyEl = recipesDocument.createElement("difficulty");
        difficultyEl.setAttribute("level", recipe.getDifficulty());
        difficultyEl.setTextContent(recipe.getDifficulty());
        recipeEl.appendChild(difficultyEl);

        // Append to root <recipes> element
        recipesDocument.getDocumentElement().appendChild(recipeEl);

        // Write updated document back to disk
        writeDocument(recipesDocument, new File(xmlDataPath, "recipes.xml"), "recipes.dtd");

        // Keep in-memory document in sync
        reloadRecipes();
    }

    private void writeDocument(Document document, File outputFile, String dtdFilename) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtdFilename);
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(outputFile);
            transformer.transform(source, result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write XML file: " + outputFile.getAbsolutePath(), e);
        }
    }
}
