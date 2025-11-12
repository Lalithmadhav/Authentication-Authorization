package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/v1/test")
public class TestController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello! You are authenticated.";
    }
}
