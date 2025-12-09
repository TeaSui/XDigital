package com.rag.contextservice.service;

import com.rag.contextservice.dto.ContextEntryCreateDto;
import com.rag.contextservice.dto.ContextEntryResponseDto;
import com.rag.contextservice.dto.ContextEntryUpdateDto;
import com.rag.contextservice.dto.PageResponseDto;
import com.rag.contextservice.model.SourceType;

import java.util.List;
import java.util.UUID;

public interface ContextEntryService {

    /**
     * Gets context entries for a session
     *
     * @param sessionId the session ID
     * @param userId    the user ID
     * @param page      the page number (0-indexed)
     * @param size      the page size
     * @param sortBy    the field to sort by
     * @param sortDirection the sort direction
     * @return the page of context entries
     */
    PageResponseDto<ContextEntryResponseDto> getContextEntries(
            UUID sessionId, UUID userId, int page, int size, String sortBy, String sortDirection);

    /**
     * Gets context entries for a session by source type
     *
     * @param sessionId  the session ID
     * @param userId     the user ID
     * @param sourceType the source type
     * @param page       the page number (0-indexed)
     * @param size       the page size
     * @param sortBy     the field to sort by
     * @param sortDirection the sort direction
     * @return the page of context entries
     */
    PageResponseDto<ContextEntryResponseDto> getContextEntriesBySourceType(
            UUID sessionId, UUID userId, SourceType sourceType, int page, int size, String sortBy, String sortDirection);

    /**
     * Creates a new context entry
     *
     * @param userId    the user ID
     * @param entryDto  the context entry create DTO
     * @return the created context entry
     */
    ContextEntryResponseDto createContextEntry(UUID userId, ContextEntryCreateDto entryDto);

    /**
     * Gets a context entry by ID
     *
     * @param id       the context entry ID
     * @param userId   the user ID
     * @return the context entry
     */
    ContextEntryResponseDto getContextEntryById(UUID id, UUID userId);

    /**
     * Updates a context entry
     *
     * @param id       the context entry ID
     * @param userId   the user ID
     * @param entryDto the context entry update DTO
     * @return the updated context entry
     */
    ContextEntryResponseDto updateContextEntry(UUID id, UUID userId, ContextEntryUpdateDto entryDto);

    /**
     * Deletes a context entry
     *
     * @param id       the context entry ID
     * @param userId   the user ID
     */
    void deleteContextEntry(UUID id, UUID userId);

    /**
     * Performs a semantic search on context entries for a session
     *
     * @param sessionId  the session ID
     * @param userId     the user ID
     * @param queryText  the query text
     * @param limit      the maximum number of results to return
     * @return list of context entries ordered by semantic similarity
     */
    List<ContextEntryResponseDto> semanticSearch(UUID sessionId, UUID userId, String queryText, int limit);

    /**
     * Gets context entry by message ID
     *
     * @param messageId the message ID
     * @param userId    the user ID
     * @return the context entry
     */
    ContextEntryResponseDto getContextEntryByMessageId(UUID messageId, UUID userId);
}