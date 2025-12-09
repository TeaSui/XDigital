package com.rag.contextservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContextEntryUpdateDto {

    @NotBlank(message = "Content is required")
    private String content;

    private Map<String, Object> metadata;

    private String text;  // Text for embedding regeneration
}