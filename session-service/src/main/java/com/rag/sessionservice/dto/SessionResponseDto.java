package com.rag.sessionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponseDto {
    private UUID id;
    private String name;
    private boolean isFavorite;
    private int messageCount;
    private OffsetDateTime lastMessageAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}