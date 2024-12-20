package com.example.helloworldapplication;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;

@RestController
@RequestMapping("/api")
public class HelloWorldController {

    private static final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);

    //@Value("${IMAGE_TAG:v1}")  // Default to v1 if IMAGE_TAG is not set
    //private String appVersion;

    @Autowired
    private CustomHealthIndicator healthIndicator;

    @GetMapping("/hello")
    public String helloWorld() {
        // Log the current version based on IMAGE_TAG
        String appVersion = System.getenv("IMAGE_TAG");
        logger.info("Currently deployed version for Hello World API: {}", appVersion);

        // Simulate failure scenario for Version 2
        if (isVersion2()) {
            logger.debug("Simulating failure in version 2");

            // Set the failure state in the health indicator
            healthIndicator.setVersion2Failure(true);

            // Log the failure and throw an exception to simulate a failure scenario
            logger.error("Version 2 failure triggered");
            throw new RuntimeException("Simulating failure in version 2");
        }

        return "Hello, World from Harness Integration version " + appVersion + "!";
    }

    // Method to determine if it's version 2
    private boolean isVersion2() {
        String imageTag = System.getenv("IMAGE_TAG");
        logger.info("IMAGE_TAG value: {}", imageTag);
        return "v2".equalsIgnoreCase(imageTag);
    }
}