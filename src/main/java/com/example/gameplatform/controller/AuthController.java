package com.example.gameplatform.controller;

import com.example.gameplatform.model.User;
import com.example.gameplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.status(201).body(userService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        return userService.findByEmail(user.getEmail())
            .map(u -> ResponseEntity.ok("JWT_TOKEN_FOR_" + u.getId()))
            .orElse(ResponseEntity.status(401).body("Invalid credentials"));
    }
}