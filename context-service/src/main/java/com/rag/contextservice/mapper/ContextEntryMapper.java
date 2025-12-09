package com.rag.contextservice.mapper;

import com.rag.contextservice.dto.ContextEntryResponseDto;
import com.rag.contextservice.model.ContextEntry;
import org.springframework.stereotype.Component;

@Component
public class ContextEntryMapper {

    /**
     * Maps a ContextEntry entity to a ContextEntryResponseDto
     *
     * @param contextEntry the context entry entity
     * @return the context entry response DTO
     */
    public ContextEntryResponseDto toResponseDto(ContextEntry contextEntry) {
        if (contextEntry == null) {
            return null;
        }

        return new ContextEntryResponseDto(
                contextEntry.getId(),
                contextEntry.getSessionId(),
                contextEntry.getUserId(),
                contextEntry.getMessageId(),
                contextEntry.getSourceType(),
                contextEntry.getContent(),
                contextEntry.getMetadata(),
                contextEntry.getCreatedAt(),
                contextEntry.getUpdatedAt()
        );
    }
}