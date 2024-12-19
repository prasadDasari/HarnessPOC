package com.example.helloworldapplication;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    private boolean version2Failed = false;

    @Override
    public Health health() {
        if (version2Failed) {
            // If version 2 failed, return DOWN status with a failure message
            return Health.down().withDetail("Error", "Version 2 failure detected").build();
        }
        // Otherwise, return UP status
        return Health.up().build();
    }

    // Method to set the failure state
    public void setVersion2Failure(boolean failure) {
        this.version2Failed = failure;
    }
}

