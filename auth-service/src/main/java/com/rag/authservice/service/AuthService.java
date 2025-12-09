package com.rag.authservice.service;

import com.rag.authservice.dto.AuthRequestDto;
import com.rag.authservice.dto.AuthResponseDto;
import com.rag.authservice.dto.RegisterRequestDto;

public interface AuthService {

    /**
     * Register a new user
     *
     * @param request the registration request
     * @return the authentication response with token
     */
    AuthResponseDto register(RegisterRequestDto request);

    /**
     * Authenticate a user
     *
     * @param request the authentication request
     * @return the authentication response with token
     */
    AuthResponseDto authenticate(AuthRequestDto request);

    /**
     * Validate a JWT token
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);

    /**
     * Extract the user ID from a JWT token
     *
     * @param token the JWT token to extract from
     * @return the user ID from the token, or null if the token is invalid
     */
    String extractUserIdFromToken(String token);
}