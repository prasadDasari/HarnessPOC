package com.example.helloworldapplication;

import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
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
    }

    @GetMapping("/hello")
    public String helloWorld() {
        String appVersion = System.getenv("IMAGE_TAG");
        logger.info("Currently deployed version for Hello World API: {}", appVersion);
        return "Hello, World from Harness Integration version " + appVersion + "!";
    }

    @GetMapping("/actuator/health")
    public Health health() {
        String appVersion = System.getenv("IMAGE_TAG");
        if ("v2".equals(appVersion)) {
            // Simulate failure for v2 by returning a DOWN status
            return Health.down().withDetail("Version", "v2 failed").build();
        }
        return Health.up().build();
    }

}