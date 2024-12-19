package com.example.helloworldapplication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class HelloWorldController {

    @GetMapping("/hello")
    public String helloWorld() {
        // Simulate failure scenario for Version 2
        if (isVersion2()) {
            throw new RuntimeException("Simulating failure in version 2");
        }
        return "Hello, World from Harness Integration Version 1!";
    }

    // Method to determine if it's version 2
    private boolean isVersion2() {
        return true;
    }
}