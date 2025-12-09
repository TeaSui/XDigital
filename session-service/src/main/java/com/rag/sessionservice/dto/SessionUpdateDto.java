package com.rag.sessionservice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionUpdateDto {
    @Size(max = 255, message = "Session name must be less than 255 characters")
    private String name;

    private Boolean isFavorite;
}