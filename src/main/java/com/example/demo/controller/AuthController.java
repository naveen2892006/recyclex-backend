package com.example.demo.controller;

import com.example.demo.dto.AuthRequestDto;
import com.example.demo.dto.AuthResponseDto;
import com.example.demo.dto.RegisterDto;
import com.example.demo.entity.SystemUser;
import com.example.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ================= REGISTER =================

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto request) {

        authService.register(request);

        return ResponseEntity.ok("Registration Successful. Please Login.");
    }

    // ================= LOGIN =================

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto request) {

        return ResponseEntity.ok(authService.login(request));
    }

    // ================= PROFILE =================

    @GetMapping("/profile/{id}")
    public ResponseEntity<SystemUser> getProfile(@PathVariable Long id) {

        return ResponseEntity.ok(authService.getProfile(id));
    }
}