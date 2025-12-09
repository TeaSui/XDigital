package com.rag.contextservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemanticSearchRequestDto {

    @NotNull(message = "Session ID is required")
    private UUID sessionId;

    @NotBlank(message = "Query text is required")
    private String queryText;

    @Min(value = 1, message = "Limit must be at least 1")
    private int limit = 5;
}