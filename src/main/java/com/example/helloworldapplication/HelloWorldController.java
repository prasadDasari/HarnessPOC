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
class HelloWorldController {

    private static final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);

    @Autowired
    private CustomHealthIndicator healthIndicator;

    @Value("${APP_VERSION:unknown}")
    private String appVersion;

    @GetMapping("/hello")
    public String helloWorld() {
        logger.info("Currently deployed version for Hello world API : {}", appVersion);

        // Simulate failure scenario for Version 2
        if (isVersion2()) {
            logger.debug("Simulating failure in version 2");
            // Set the failure state in the health indicator
            healthIndicator.setVersion2Failure(true);
            throw new RuntimeException("Simulating failure in version 2");
        }

        return "Hello, World from Harness Integration version " + appVersion + "!";
    }

    // Method to determine if it's version 2
    private boolean isVersion2() {
        return "v2".equalsIgnoreCase(appVersion);
    }
}