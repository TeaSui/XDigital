package com.rag.messageservice.dto;

import com.rag.messageservice.model.MessageRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDto {

    private UUID id;
    private UUID sessionId;
    private UUID userId;
    private MessageRole role;
    private String content;
    private int tokenCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}