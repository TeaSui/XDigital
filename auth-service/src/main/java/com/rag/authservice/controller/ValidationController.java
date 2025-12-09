package com.rag.authservice.controller;

import com.rag.authservice.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/validate")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "API key validation endpoints")
public class ValidationController {

    private final ApiKeyService apiKeyService;

    @GetMapping
    @Operation(summary = "Validate API key")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API key validation result")
    })
    public ResponseEntity<String> validateApiKey(
            @Parameter(description = "API key to validate")
            @RequestParam String apiKey) {
        log.info("Validating API key");
        String userId = apiKeyService.validateApiKey(apiKey);
        return ResponseEntity.ok(userId);
    }
}