package com.demo.controller;

import com.demo.dto.AuthResponseDTO;
import com.demo.dto.LoginRequestDTO;
import com.demo.dto.RegisterRequestDTO;
import com.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // allow frontend to call — restrict this in production
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register
    // Body: { "name": "John", "email": "john@gmail.com", "password": "123456" }
    // Returns: { "token": "...", "name": "John", "email": "...", "role": "USER" }
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // POST /api/auth/login
    // Body: { "email": "john@gmail.com", "password": "123456" }
    // Returns: { "token": "...", "name": "John", "email": "...", "role": "USER" }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}