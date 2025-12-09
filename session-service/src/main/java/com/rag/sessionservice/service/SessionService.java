package com.rag.sessionservice.service;

import com.rag.sessionservice.dto.PageResponseDto;
import com.rag.sessionservice.dto.SessionCreateDto;
import com.rag.sessionservice.dto.SessionResponseDto;
import com.rag.sessionservice.dto.SessionUpdateDto;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface SessionService {

    /**
     * Gets all sessions for a user
     *
     * @param userId the user ID
     * @param page the page number (0-indexed)
     * @param size the page size
     * @param isFavorite filter by favorite status (null to get all)
     * @param sortBy the field to sort by
     * @param sortDirection the sort direction
     * @return the page of sessions
     */
    PageResponseDto<SessionResponseDto> getSessions(
            UUID userId, int page, int size, Boolean isFavorite, String sortBy, String sortDirection);

    /**
     * Creates a new session
     *
     * @param userId the user ID
     * @param sessionDto the session create DTO
     * @return the created session
     */
    SessionResponseDto createSession(UUID userId, SessionCreateDto sessionDto);

    /**
     * Gets a session by ID
     *
     * @param id the session ID
     * @param userId the user ID
     * @return the session
     * @throws com.rag.sessionservice.exception.ResourceNotFoundException if session not found
     */
    SessionResponseDto getSessionById(UUID id, UUID userId);

    /**
     * Updates a session
     *
     * @param id the session ID
     * @param userId the user ID
     * @param sessionDto the session update DTO
     * @return the updated session
     * @throws com.rag.sessionservice.exception.ResourceNotFoundException if session not found
     */
    SessionResponseDto updateSession(UUID id, UUID userId, SessionUpdateDto sessionDto);

    /**
     * Deletes a session
     *
     * @param id the session ID
     * @param userId the user ID
     * @throws com.rag.sessionservice.exception.ResourceNotFoundException if session not found
     */
    void deleteSession(UUID id, UUID userId);

    /**
     * Increments the message count for a session
     *
     * @param sessionId the session ID
     * @param timestamp the timestamp of the message
     * @return the updated session
     */
    SessionResponseDto incrementMessageCount(UUID sessionId, OffsetDateTime timestamp);

    /**
     * Checks if a session exists for a user
     *
     * @param id the session ID
     * @param userId the user ID
     * @return true if the session exists, false otherwise
     */
    boolean existsSessionByIdAndUserId(UUID id, UUID userId);
}