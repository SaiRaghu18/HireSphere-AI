package com.hirsphere.backend.controller;

import com.hirsphere.backend.dto.RegisterRequest;
import com.hirsphere.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hirsphere.backend.dto.AuthResponse;
import com.hirsphere.backend.dto.LoginRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {

        authService.register(request);

        return ResponseEntity.ok("User Registered Successfully");
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));

    }

    @GetMapping("/health")
    public ResponseEntity<java.util.Map<String, String>> health() {
        java.util.Map<String, String> response = new java.util.HashMap<>();
        response.put("status", "UP");
        response.put("service", "HireSphere.ai");
        return ResponseEntity.ok(response);
    }
}