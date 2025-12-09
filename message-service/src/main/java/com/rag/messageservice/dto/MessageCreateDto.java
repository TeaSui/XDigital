package com.rag.messageservice.dto;

import com.rag.messageservice.model.MessageRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageCreateDto {

    @NotNull(message = "Session ID is required")
    private UUID sessionId;

    @NotNull(message = "Role is required")
    private MessageRole role;

    @NotBlank(message = "Content is required")
    private String content;
}