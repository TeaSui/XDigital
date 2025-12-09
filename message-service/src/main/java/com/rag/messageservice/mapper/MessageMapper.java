package com.rag.messageservice.mapper;

import com.rag.messageservice.dto.MessageResponseDto;
import com.rag.messageservice.model.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    /**
     * Maps a Message entity to a MessageResponseDto
     *
     * @param message the message entity
     * @return the message response DTO
     */
    public MessageResponseDto toResponseDto(Message message) {
        if (message == null) {
            return null;
        }

        return new MessageResponseDto(
                message.getId(),
                message.getSessionId(),
                message.getUserId(),
                message.getRole(),
                message.getContent(),
                message.getTokenCount(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}