package com.example.helloworldapplication;

import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        String appVersion = System.getenv("IMAGE_TAG");
        logger.info("Currently deployed version for Hello World API: {}", appVersion);
    }

    @GetMapping("/hello")
    public String helloWorld() {
        String appVersion = System.getenv("IMAGE_TAG");
        logger.info("Currently deployed version for Hello World API: {}", appVersion);
        return "Hello, World from Harness Integration version " + appVersion + "!";
    }

    @GetMapping("/health/heartbeat")
    public ResponseEntity<String> healthCheck() {
        // Get the app version from the environment variable
        String appVersion = System.getenv("IMAGE_TAG");

        // Log the app version to help with troubleshooting
        logger.info("Current app version: {}", appVersion);

        // If the app version is v2, return a failure (503 Service Unavailable)
        if ("v2".equals(appVersion)) {
            logger.error("Service is unavailable. Version: v2");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service unavailable for Version " + appVersion);
        } else {
            // If the app version is v1, return a success (200 OK)
            logger.info("Service is up. Version: {}", appVersion);
            return ResponseEntity.ok("Service is up");
        }
    }

}