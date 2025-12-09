package com.rag.authservice.controller;

import com.rag.authservice.dto.ApiKeyRequestDto;
import com.rag.authservice.dto.ApiKeyResponseDto;
import com.rag.authservice.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/keys")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "API Keys", description = "API key management endpoints")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    @Operation(summary = "Generate new API key")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "API key created"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    public ResponseEntity<ApiKeyResponseDto> generateApiKey(@Valid @RequestBody ApiKeyRequestDto request) {
        log.info("Generating API key for username: {}", request.getUsername());
        ApiKeyResponseDto response = apiKeyService.generateApiKey(request.getUsername());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getUserId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Revoke API key")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "API key revoked"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<Void> revokeApiKey(@PathVariable UUID id) {
        log.info("Revoking API key for user ID: {}", id);
        apiKeyService.revokeApiKey(id);
        return ResponseEntity.noContent().build();
    }
}