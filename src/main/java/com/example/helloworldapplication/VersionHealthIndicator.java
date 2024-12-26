package com.example.helloworldapplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class VersionHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(VersionHealthIndicator.class);

    @Override
    public Health health() {
        // Retrieve the IMAGE_TAG environment variable
        String appVersion = System.getenv("IMAGE_TAG");

        // Log the version being checked
        logger.info("Checking health for application version: {}", appVersion);

        // Check for version v2 and simulate failure
        if ("v2".equalsIgnoreCase(appVersion)) {
            // Log that version v2 is causing the failure
            logger.error("Version v2 failure triggered: returning DOWN status");

            // Simulate failure for version v2
            return Health.down().withDetail("Version", "v2").build();
        }

        // For any other version, consider it healthy
        logger.info("Version is not v2, returning UP status");

        return Health.up().build();
    }
}
