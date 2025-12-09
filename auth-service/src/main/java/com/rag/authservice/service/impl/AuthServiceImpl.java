package com.rag.authservice.service.impl;

import com.rag.authservice.dto.AuthRequestDto;
import com.rag.authservice.dto.AuthResponseDto;
import com.rag.authservice.dto.RegisterRequestDto;
import com.rag.authservice.exception.ResourceNotFoundException;
import com.rag.authservice.model.User;
import com.rag.authservice.repository.UserRepository;
import com.rag.authservice.security.JwtUtil;
import com.rag.authservice.service.AuthService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Username '{}' already exists", request.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        // Create new user with API key hash initialized to empty
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setApiKeyHash(passwordEncoder.encode("default-api-key"));

        // Save user
        User savedUser = userRepository.save(user);
        log.info("User registered: {}", savedUser.getUsername());

        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getId());

        return new AuthResponseDto(savedUser.getId(), savedUser.getUsername(), token);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto authenticate(AuthRequestDto request) {
        // Find user by username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("User '{}' not found during authentication", request.getUsername());
                    return new ResourceNotFoundException("User not found");
                });

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", user.getUsername());
            throw new BadCredentialsException("Invalid credentials");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        log.info("User authenticated: {}", user.getUsername());

        return new AuthResponseDto(user.getId(), user.getUsername(), token);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    @Override
    public String extractUserIdFromToken(String token) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return null;
            }

            // Extract userId from token claims
            Claims claims = jwtUtil.extractAllClaims(token);
            String userId = claims.get("userId", String.class);

            log.info("Extracted user ID from token: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("Error extracting user ID from token: {}", e.getMessage());
            return null;
        }
    }
}