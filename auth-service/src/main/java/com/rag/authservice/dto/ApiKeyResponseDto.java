package com.rag.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyResponseDto {
    private UUID userId;
    private String apiKey;
    private String username;
}