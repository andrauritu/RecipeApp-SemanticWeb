package com.example.reciperecommender.validation;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;

@Service
@DependsOn("recipeScraperService")
public class XmlValidatorService {

    @Value("${xml.data.path}")
    private String xmlDataPath;

    @PostConstruct
    public void validateAll() {
        validateXml(xmlDataPath + "/recipes.xml", xmlDataPath + "/recipes.dtd");
        validateXml(xmlDataPath + "/users.xml", xmlDataPath + "/users.dtd");
    }

    public void validateXml(String xmlFilePath, String dtdFilePath) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            final boolean[] failed = {false};

            reader.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException e) {
                    System.out.println("[Validator] WARNING in " + xmlFilePath + ": " + e.getMessage());
                }

                @Override
                public void error(SAXParseException e) {
                    System.out.println("[Validator] ERROR in " + xmlFilePath + ": " + e.getMessage());
                    failed[0] = true;
                }

                @Override
                public void fatalError(SAXParseException e) {
                    System.out.println("[Validator] FATAL in " + xmlFilePath + ": " + e.getMessage());
                    failed[0] = true;
                }
            });

            InputSource inputSource = new InputSource(new FileInputStream(xmlFilePath));
            inputSource.setSystemId(new File(xmlFilePath).toURI().toString());
            reader.parse(inputSource);

            if (!failed[0]) {
                System.out.println("[Validator] OK: " + xmlFilePath + " is valid.");
            } else {
                System.out.println("[Validator] FAILED: " + xmlFilePath + " is invalid.");
            }

        } catch (Exception e) {
            System.out.println("[Validator] FAILED: " + xmlFilePath + " — " + e.getMessage());
        }
    }
}
