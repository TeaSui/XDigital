package com.rag.sessionservice.mapper;

import com.rag.sessionservice.dto.SessionResponseDto;
import com.rag.sessionservice.model.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper {

    /**
     * Maps a Session entity to a SessionResponseDto
     *
     * @param session the session entity
     * @return the session response DTO
     */
    public SessionResponseDto toResponseDto(Session session) {
        if (session == null) {
            return null;
        }

        return new SessionResponseDto(
                session.getId(),
                session.getName(),
                session.isFavorite(),
                session.getMessageCount(),
                session.getLastMessageAt(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}