package com.demo.auth.controller;

import com.demo.auth.dto.response.AuthResponseDTO;
import com.demo.auth.dto.request.LoginRequestDTO;
import com.demo.auth.dto.request.RegisterRequestDTO;
import com.demo.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import com.demo.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // allow frontend to call — restrict this in production
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;                      // ← NEW
    private final UserDetailsService userDetailsService; // ← NEW

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

    /**
     * NEW: POST /api/auth/logout
     * Body: Bearer token in Authorization header
     * Invalidates the token so it can't be used anymore
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    /**
     * NEW: POST /api/auth/validate-token
     * Body: Bearer token in Authorization header
     * Returns 200 if token is valid, 401 if invalid/expired
     * Frontend calls this on app startup to verify token
     */
    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("No token provided"));
        }

        try {
            String token = authHeader.substring(7);

            // Extract email from token
            String email = jwtUtil.extractEmail(token);

            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Validate token
            if (!jwtUtil.isTokenValid(token, userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Token is invalid or expired"));
            }

            // Token is valid — return user info
            return ResponseEntity.ok(new TokenValidationResponse(
                    true,
                    email,
                    userDetails.getUsername()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Token validation failed: " + e.getMessage()));
        }
    }

    /**
     * Helper DTOs
     */
    public static class MessageResponse {
        public String message;
        public MessageResponse(String message) {
            this.message = message;
        }
    }

    public static class TokenValidationResponse {
        public boolean valid;
        public String email;
        public String username;

        public TokenValidationResponse(boolean valid, String email, String username) {
            this.valid = valid;
            this.email = email;
            this.username = username;
        }
    }
}