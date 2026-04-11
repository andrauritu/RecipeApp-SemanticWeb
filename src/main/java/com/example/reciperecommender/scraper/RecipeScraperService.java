package com.example.reciperecommender.scraper;

import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class RecipeScraperService {

    private static final String SCRAPE_URL =
            "https://www.bbcgoodfood.com/recipes/collection/budget-autumn";

    private static final String[] CUISINE_TYPES = {
            "Italian", "Asian", "French", "Mexican",
            "Mediterranean", "Indian", "American", "Japanese"
    };

    private static final String[] DIFFICULTY_LEVELS = {
            "Beginner", "Intermediate", "Advanced"
    };

    private static final Random RANDOM = new Random(42L);

    @Value("${xml.data.path}")
    private String xmlDataPath;

    @PostConstruct
    public void initXmlData() {
        File xmlDir = new File(xmlDataPath);
        if (!xmlDir.exists()) {
            xmlDir.mkdirs();
        }

        File recipesFile = new File(xmlDir, "recipes.xml");
        File usersFile = new File(xmlDir, "users.xml");

        if (!recipesFile.exists()) {
            List<String> titles = scrapeRecipeTitles();
            writeRecipesXml(titles, recipesFile);
            System.out.println("[Scraper] Saved " + titles.size() + " recipes to " + recipesFile.getAbsolutePath());
        } else {
            System.out.println("[Scraper] recipes.xml already exists — skipping scrape.");
        }

        if (!usersFile.exists()) {
            writeUsersXml(usersFile);
            System.out.println("[Scraper] Created users.xml at " + usersFile.getAbsolutePath());
        } else {
            System.out.println("[Scraper] users.xml already exists — skipping.");
        }
    }

    private List<String> scrapeRecipeTitles() {
        List<String> titles = new ArrayList<>();

        try {
            System.out.println("[Scraper] Fetching: " + SCRAPE_URL);

            Document doc = Jsoup.connect(SCRAPE_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                               "AppleWebKit/537.36 (KHTML, like Gecko) " +
                               "Chrome/120.0.0.0 Safari/537.36")
                    .referrer("https://www.google.com")
                    .timeout(15_000)
                    .get();

            String[] selectors = {
                    "h2.card__title",
                    ".card__title",
                    "[data-testid='card-title']",
                    "h2.heading-4",
                    ".heading-4",
                    "[class*='card'] h2",
                    "article h2",
                    "h2"
            };

            for (String selector : selectors) {
                Elements elements = doc.select(selector);
                for (Element el : elements) {
                    String text = el.text().trim();
                    if (!text.isEmpty() && !titles.contains(text)) {
                        titles.add(text);
                    }
                }
                if (titles.size() >= 20) {
                    System.out.println("[Scraper] Found " + titles.size()
                            + " titles using selector: " + selector);
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("[Scraper] Failed to fetch page: " + e.getMessage());
        }

        if (titles.size() < 20) {
            System.err.println("[Scraper] WARNING: only found " + titles.size()
                    + " titles. Padding with fallback entries.");
            String[] fallback = {
                    "Spaghetti Bolognese", "Chicken Tikka Masala", "Beef Stew",
                    "Vegetable Curry", "Mushroom Risotto", "Lentil Soup",
                    "Pasta Primavera", "Fish and Chips", "Lamb Tagine",
                    "Pumpkin Soup", "Apple Crumble", "Leek and Potato Soup",
                    "Pork Casserole", "Sweet Potato Pie", "Autumn Minestrone",
                    "Butternut Squash Soup", "Roast Chicken", "Cheese and Onion Pie",
                    "Toad in the Hole", "Parsnip and Carrot Soup"
            };
            for (String fb : fallback) {
                if (!titles.contains(fb)) {
                    titles.add(fb);
                }
                if (titles.size() >= 20) break;
            }
        }

        return titles;
    }

    private void writeRecipesXml(List<String> titles, File outputFile) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<!DOCTYPE recipes SYSTEM \"recipes.dtd\">\n");
        sb.append("<recipes>\n");

        for (int i = 0; i < titles.size(); i++) {
            int id = i + 1;
            String title = escapeXml(titles.get(i));

            int idx1 = RANDOM.nextInt(CUISINE_TYPES.length);
            int idx2;
            do {
                idx2 = RANDOM.nextInt(CUISINE_TYPES.length);
            } while (idx2 == idx1);

            String cuisine1 = CUISINE_TYPES[idx1];
            String cuisine2 = CUISINE_TYPES[idx2];
            String difficulty = DIFFICULTY_LEVELS[RANDOM.nextInt(DIFFICULTY_LEVELS.length)];

            sb.append("  <recipe id=\"").append(id).append("\">\n");
            sb.append("    <title>").append(title).append("</title>\n");
            sb.append("    <cuisineTypes>\n");
            sb.append("      <cuisineType value=\"").append(cuisine1).append("\">").append(cuisine1).append("</cuisineType>\n");
            sb.append("      <cuisineType value=\"").append(cuisine2).append("\">").append(cuisine2).append("</cuisineType>\n");
            sb.append("    </cuisineTypes>\n");
            sb.append("    <difficulty level=\"").append(difficulty).append("\">").append(difficulty).append("</difficulty>\n");
            sb.append("  </recipe>\n");
        }

        sb.append("</recipes>\n");

        try (FileWriter fw = new FileWriter(outputFile)) {
            fw.write(sb.toString());
        } catch (IOException e) {
            System.err.println("[Scraper] Failed to write recipes.xml: " + e.getMessage());
        }
    }

    private void writeUsersXml(File outputFile) {
        String content =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE users SYSTEM \"users.dtd\">\n" +
                "<users>\n" +
                "  <user id=\"1\">\n" +
                "    <name>John</name>\n" +
                "    <surname>Doe</surname>\n" +
                "    <cookingSkillLevel value=\"Beginner\">Beginner</cookingSkillLevel>\n" +
                "    <preferredCuisineType value=\"Italian\">Italian</preferredCuisineType>\n" +
                "  </user>\n" +
                "</users>\n";

        try (FileWriter fw = new FileWriter(outputFile)) {
            fw.write(content);
        } catch (IOException e) {
            System.err.println("[Scraper] Failed to write users.xml: " + e.getMessage());
        }
    }

    private String escapeXml(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
