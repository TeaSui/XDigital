package com.rag.authservice.service;

import com.rag.authservice.dto.ApiKeyResponseDto;
import com.rag.authservice.model.User;

import java.util.UUID;

public interface ApiKeyService {

    /**
     * Generates a new API key for the given username
     *
     * @param username the username to generate an API key for
     * @return the generated API key response
     */
    ApiKeyResponseDto generateApiKey(String username);

    /**
     * Validates if the given API key is valid
     *
     * @param apiKey the API key to validate
     * @return the user ID if the API key is valid, null otherwise
     */
    String validateApiKey(String apiKey);

    /**
     * Revokes the API key for the given user ID
     *
     * @param userId the user ID to revoke the API key for
     */
    void revokeApiKey(UUID userId);
}