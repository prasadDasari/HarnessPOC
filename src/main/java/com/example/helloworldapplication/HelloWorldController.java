package com.example.helloworldapplication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class HelloWorldController {
    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello, World from Harness Integration Version 1!";
    }
}