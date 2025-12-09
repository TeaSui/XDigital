package com.rag.authservice.service.impl;

import com.rag.authservice.dto.ApiKeyResponseDto;
import com.rag.authservice.exception.ResourceNotFoundException;
import com.rag.authservice.model.User;
import com.rag.authservice.repository.UserRepository;
import com.rag.authservice.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyServiceImpl implements ApiKeyService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ApiKeyResponseDto generateApiKey(String username) {
        String apiKey = UUID.randomUUID().toString();
        String hashedApiKey = passwordEncoder.encode(apiKey);

        User user = userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username);
                    // Set default values for new fields - these should be properly set by an auth controller
                    newUser.setEmail(username + "@example.com");
                    newUser.setPassword(passwordEncoder.encode("default"));
                    return newUser;
                });

        user.setApiKeyHash(hashedApiKey);
        User savedUser = userRepository.save(user);

        log.info("Generated API key for user: {}", username);
        return new ApiKeyResponseDto(savedUser.getId(), apiKey, savedUser.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public String validateApiKey(String apiKey) {
        log.info("Validating API key");

        for (User user : userRepository.findAll()) {
            if (passwordEncoder.matches(apiKey, user.getApiKeyHash())) {
                log.info("API key is valid for user: {}", user.getUsername());
                return user.getId().toString();
            }
        }

        log.info("API key is invalid");
        return null;
    }

    @Override
    @Transactional
    public void revokeApiKey(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        userRepository.delete(user);
        log.info("Revoked API key for user: {}", user.getUsername());
    }
}