package com.rag.authservice.controller;

import com.rag.authservice.dto.AuthRequestDto;
import com.rag.authservice.dto.AuthResponseDto;
import com.rag.authservice.dto.RegisterRequestDto;
import com.rag.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication endpoints for user login and registration")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or username already exists")
    })
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        log.info("Registering user: {}", request.getUsername());
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
        log.info("Authenticating user: {}", request.getUsername());
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate JWT token and return user ID if valid")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User ID if token is valid"),
            @ApiResponse(responseCode = "401", description = "Invalid token")
    })
    public ResponseEntity<String> validateToken(@RequestParam String token) {
        log.info("Validating JWT token");
        String userId = authService.extractUserIdFromToken(token);
        if (userId != null) {
            log.info("Token validated successfully for user: {}", userId);
            return ResponseEntity.ok(userId);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}