package com.example.helloworldapplication;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/api")
public class HelloWorldController {

    private static final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);

    @PostConstruct
    public void init() {
        logger.info("Initializing HelloWorldController....");
        logger.info("IMAGE_TAG environment variable: {}", System.getenv("IMAGE_TAG"));
        String appVersion = System.getenv("IMAGE_TAG");
        logger.info("Currently deployed version for Hello World API: {}", appVersion);

        // Simulate failure scenario for Version 2 during app startup
        if (isVersion2(appVersion)) {
            logger.debug("Simulating failure in version 2");

            // Log the failure and throw an exception to simulate a failure scenario
            logger.error("Version 2 failure triggered at startup");
            throw new RuntimeException("Simulating failure in version 2 at startup");
        }
    }

    @GetMapping("/hello")
    public String helloWorld() {
        String appVersion = System.getenv("IMAGE_TAG");
        logger.info("Currently deployed version for Hello World API: {}", appVersion);
        return "Hello, World from Harness Integration version " + appVersion + "!";
    }

    // Method to determine if it's version 2 based on the appVersion
    private boolean isVersion2(String appVersion) {
        logger.info("IMAGE_TAG value: {}", appVersion);
        return "v2".equalsIgnoreCase(appVersion);
    }

}