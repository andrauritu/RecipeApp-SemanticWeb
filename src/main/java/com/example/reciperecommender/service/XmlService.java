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
import javax.xml.transform.stream.StreamSource;
import java.io.StringWriter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
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

    public User getFirstUser() {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            Element userEl = (Element) xpath.evaluate(
                    "/users/user[1]", usersDocument, XPathConstants.NODE);
            if (userEl == null) {
                return null;
            }
            String id = userEl.getAttribute("id");
            String name = userEl.getElementsByTagName("name").item(0).getTextContent().trim();
            String surname = userEl.getElementsByTagName("surname").item(0).getTextContent().trim();
            String cookingSkillLevel = userEl.getElementsByTagName("cookingSkillLevel").item(0).getTextContent().trim();
            String preferredCuisineType = userEl.getElementsByTagName("preferredCuisineType").item(0).getTextContent().trim();
            return new User(id, name, surname, cookingSkillLevel, preferredCuisineType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get first user via XPath", e);
        }
    }

    public List<Recipe> getRecipesBySkillLevel(String skillLevel) {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            String expression = String.format("/recipes/recipe[difficulty='%s']", skillLevel);
            NodeList nodes = (NodeList) xpath.evaluate(
                    expression, recipesDocument, XPathConstants.NODESET);

            List<Recipe> recipes = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                Element recipeEl = (Element) nodes.item(i);
                String id = recipeEl.getAttribute("id");
                String title = recipeEl.getElementsByTagName("title").item(0).getTextContent().trim();

                List<String> cuisineTypes = new ArrayList<>();
                NodeList cuisineNodes = recipeEl.getElementsByTagName("cuisineType");
                for (int j = 0; j < cuisineNodes.getLength(); j++) {
                    cuisineTypes.add(cuisineNodes.item(j).getTextContent().trim());
                }

                String difficulty = recipeEl.getElementsByTagName("difficulty").item(0).getTextContent().trim();
                recipes.add(new Recipe(id, title, cuisineTypes, difficulty));
            }
            return recipes;
        } catch (Exception e) {
            throw new RuntimeException("Failed to query recipes by skill level: " + skillLevel, e);
        }
    }

    public List<Recipe> getRecipesByCuisineType(String cuisineType) {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            String expression = String.format(
                    "/recipes/recipe[cuisineTypes/cuisineType='%s']", cuisineType);
            NodeList nodes = (NodeList) xpath.evaluate(
                    expression, recipesDocument, XPathConstants.NODESET);

            List<Recipe> recipes = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                Element recipeEl = (Element) nodes.item(i);
                String id = recipeEl.getAttribute("id");
                String title = recipeEl.getElementsByTagName("title").item(0).getTextContent().trim();

                List<String> cuisineTypes = new ArrayList<>();
                NodeList cuisineNodes = recipeEl.getElementsByTagName("cuisineType");
                for (int j = 0; j < cuisineNodes.getLength(); j++) {
                    cuisineTypes.add(cuisineNodes.item(j).getTextContent().trim());
                }

                String difficulty = recipeEl.getElementsByTagName("difficulty").item(0).getTextContent().trim();
                recipes.add(new Recipe(id, title, cuisineTypes, difficulty));
            }
            return recipes;
        } catch (Exception e) {
            throw new RuntimeException("Failed to query recipes by cuisine type: " + cuisineType, e);
        }
    }

    public Recipe getRecipeById(String id) {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            String expression = String.format("/recipes/recipe[@id='%s']", id);
            Element recipeEl = (Element) xpath.evaluate(
                    expression, recipesDocument, XPathConstants.NODE);
            if (recipeEl == null) {
                return null;
            }
            String title = recipeEl.getElementsByTagName("title").item(0).getTextContent().trim();

            List<String> cuisineTypes = new ArrayList<>();
            NodeList cuisineNodes = recipeEl.getElementsByTagName("cuisineType");
            for (int j = 0; j < cuisineNodes.getLength(); j++) {
                cuisineTypes.add(cuisineNodes.item(j).getTextContent().trim());
            }

            String difficulty = recipeEl.getElementsByTagName("difficulty").item(0).getTextContent().trim();
            return new Recipe(id, title, cuisineTypes, difficulty);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get recipe by id: " + id, e);
        }
    }

    public List<Recipe> getRecipesBySkillLevelAndCuisine(String skillLevel, String cuisineType) {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            String expression = String.format(
                    "/recipes/recipe[difficulty='%s' and cuisineTypes/cuisineType='%s']",
                    skillLevel, cuisineType);
            NodeList nodes = (NodeList) xpath.evaluate(
                    expression, recipesDocument, XPathConstants.NODESET);

            List<Recipe> recipes = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                Element recipeEl = (Element) nodes.item(i);
                String id = recipeEl.getAttribute("id");
                String title = recipeEl.getElementsByTagName("title").item(0).getTextContent().trim();

                List<String> cuisineTypes = new ArrayList<>();
                NodeList cuisineNodes = recipeEl.getElementsByTagName("cuisineType");
                for (int j = 0; j < cuisineNodes.getLength(); j++) {
                    cuisineTypes.add(cuisineNodes.item(j).getTextContent().trim());
                }

                String difficulty = recipeEl.getElementsByTagName("difficulty").item(0).getTextContent().trim();
                recipes.add(new Recipe(id, title, cuisineTypes, difficulty));
            }
            return recipes;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to query recipes by skill level and cuisine: " + skillLevel + ", " + cuisineType, e);
        }
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

    public void addUser(User user) {
        // Generate new id = max existing id + 1
        NodeList userNodes = usersDocument.getElementsByTagName("user");
        int maxId = 0;
        for (int i = 0; i < userNodes.getLength(); i++) {
            Element el = (Element) userNodes.item(i);
            try {
                int id = Integer.parseInt(el.getAttribute("id"));
                if (id > maxId) maxId = id;
            } catch (NumberFormatException ignored) {}
        }
        String newId = String.valueOf(maxId + 1);
        user.setId(newId);

        // Build new <user> DOM element
        Element userEl = usersDocument.createElement("user");
        userEl.setAttribute("id", newId);

        Element nameEl = usersDocument.createElement("name");
        nameEl.setTextContent(user.getName());
        userEl.appendChild(nameEl);

        Element surnameEl = usersDocument.createElement("surname");
        surnameEl.setTextContent(user.getSurname());
        userEl.appendChild(surnameEl);

        Element skillEl = usersDocument.createElement("cookingSkillLevel");
        skillEl.setAttribute("value", user.getCookingSkillLevel());
        skillEl.setTextContent(user.getCookingSkillLevel());
        userEl.appendChild(skillEl);

        Element cuisineEl = usersDocument.createElement("preferredCuisineType");
        cuisineEl.setAttribute("value", user.getPreferredCuisineType());
        cuisineEl.setTextContent(user.getPreferredCuisineType());
        userEl.appendChild(cuisineEl);

        // Append to root <users> element
        usersDocument.getDocumentElement().appendChild(userEl);

        // Write updated document back to disk
        writeDocument(usersDocument, new File(xmlDataPath, "users.xml"), "users.dtd");

        // Keep in-memory document in sync
        reloadUsers();
    }

    public String transformRecipesWithXsl(String userSkillLevel) {
        try {
            File xslFile = new File(xmlDataPath, "recipes.xsl");
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xslFile));
            transformer.setParameter("userSkill", userSkillLevel);

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(recipesDocument), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply XSL transformation", e);
        }
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
