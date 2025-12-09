package com.rag.messageservice.service;

import com.rag.messageservice.dto.MessageCreateDto;
import com.rag.messageservice.dto.MessageResponseDto;
import com.rag.messageservice.dto.MessageUpdateDto;
import com.rag.messageservice.dto.PageResponseDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    /**
     * Gets messages for a session
     *
     * @param sessionId the session ID
     * @param userId    the user ID
     * @param page      the page number (0-indexed)
     * @param size      the page size
     * @param sortBy    the field to sort by
     * @param sortDirection the sort direction
     * @return the page of messages
     */
    PageResponseDto<MessageResponseDto> getMessages(
            UUID sessionId, UUID userId, int page, int size, String sortBy, String sortDirection);

    /**
     * Gets all messages for a session
     *
     * @param sessionId the session ID
     * @param userId    the user ID
     * @return the list of messages
     */
    List<MessageResponseDto> getAllSessionMessages(UUID sessionId, UUID userId);

    /**
     * Creates a new message
     *
     * @param userId      the user ID
     * @param messageDto  the message create DTO
     * @return the created message
     */
    MessageResponseDto createMessage(UUID userId, MessageCreateDto messageDto);

    /**
     * Gets a message by ID
     *
     * @param id       the message ID
     * @param userId   the user ID
     * @return the message
     */
    MessageResponseDto getMessageById(UUID id, UUID userId);

    /**
     * Updates a message
     *
     * @param id         the message ID
     * @param userId     the user ID
     * @param messageDto the message update DTO
     * @return the updated message
     */
    MessageResponseDto updateMessage(UUID id, UUID userId, MessageUpdateDto messageDto);

    /**
     * Deletes a message
     *
     * @param id       the message ID
     * @param userId   the user ID
     */
    void deleteMessage(UUID id, UUID userId);

    /**
     * Gets the token count for a message
     *
     * @param content the message content
     * @return the token count
     */
    int countTokens(String content);
}