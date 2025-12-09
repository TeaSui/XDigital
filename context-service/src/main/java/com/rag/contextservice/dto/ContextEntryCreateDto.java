package com.rag.contextservice.dto;

import com.rag.contextservice.model.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContextEntryCreateDto {

    @NotNull(message = "Session ID is required")
    private UUID sessionId;

    private UUID messageId;

    @NotNull(message = "Source type is required")
    private SourceType sourceType;

    @NotBlank(message = "Content is required")
    private String content;

    private Map<String, Object> metadata;

    private String text;  // Text for embedding generation
}