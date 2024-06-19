package com.example.securitylogin.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    @PostMapping("/admin")
    public String adminP(){
        return "Admin Page";
    }
}
