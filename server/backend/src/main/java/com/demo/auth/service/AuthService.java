package com.demo.auth.service;

import com.demo.auth.dto.response.AuthResponseDTO;
import com.demo.auth.dto.request.LoginRequestDTO;
import com.demo.auth.dto.request.RegisterRequestDTO;
import com.demo.auth.model.Role;
import com.demo.auth.model.User;
import com.demo.auth.repository.UserRepository;
import com.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;  // ← NEW

    public AuthResponseDTO register(RegisterRequestDTO request) {

        // check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        // build and save new user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt encode
                .role(Role.USER)
                .build();

        userRepository.save(user);

        // generate JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponseDTO.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponseDTO login(LoginRequestDTO request) {

        // authenticate — throws exception if credentials are wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // credentials valid — load user and generate token
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponseDTO.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    /**
     * NEW: Logout method
     * Adds token to blacklist so it can't be used anymore
     */
    public void logout(String token) {
        if (token != null && !token.isEmpty()) {
            // Remove "Bearer " prefix if present
            String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            tokenBlacklistService.blacklistToken(cleanToken);
        }
    }
}