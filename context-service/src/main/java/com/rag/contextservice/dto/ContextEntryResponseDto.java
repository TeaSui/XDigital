package com.rag.contextservice.dto;

import com.rag.contextservice.model.SourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContextEntryResponseDto {

    private UUID id;
    private UUID sessionId;
    private UUID userId;
    private UUID messageId;
    private SourceType sourceType;
    private String content;
    private Map<String, Object> metadata;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}