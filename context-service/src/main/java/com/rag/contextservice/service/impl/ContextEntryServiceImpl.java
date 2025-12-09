package com.rag.contextservice.service.impl;

import com.rag.contextservice.dto.ContextEntryCreateDto;
import com.rag.contextservice.dto.ContextEntryResponseDto;
import com.rag.contextservice.dto.ContextEntryUpdateDto;
import com.rag.contextservice.dto.PageResponseDto;
import com.rag.contextservice.event.ContextEventProducer;
import com.rag.contextservice.exception.ResourceNotFoundException;
import com.rag.contextservice.mapper.ContextEntryMapper;
import com.rag.contextservice.model.ContextEntry;
import com.rag.contextservice.model.SourceType;
import com.rag.contextservice.repository.ContextEntryRepository;
import com.rag.contextservice.service.ContextEntryService;
import com.rag.contextservice.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ContextEntryServiceImpl implements ContextEntryService {

    private final ContextEntryRepository contextEntryRepository;
    private final ContextEntryMapper contextEntryMapper;
    private final ContextEventProducer eventProducer;
    private final EmbeddingService embeddingService;

    @Override
    public PageResponseDto<ContextEntryResponseDto> getContextEntries(
            UUID sessionId, UUID userId, int page, int size, String sortBy, String sortDirection) {

        log.info("Getting context entries for session ID: {} and user ID: {}", sessionId, userId);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<ContextEntry> entryPage = contextEntryRepository.findBySessionIdAndUserId(sessionId, userId, pageRequest);
        Page<ContextEntryResponseDto> dtoPage = entryPage.map(contextEntryMapper::toResponseDto);
        return PageResponseDto.fromPage(dtoPage);
    }

    @Override
    public PageResponseDto<ContextEntryResponseDto> getContextEntriesBySourceType(
            UUID sessionId, UUID userId, SourceType sourceType, int page, int size, String sortBy, String sortDirection) {

        log.info("Getting context entries for session ID: {}, user ID: {}, and source type: {}",
                sessionId, userId, sourceType);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<ContextEntry> entryPage = contextEntryRepository.findBySessionIdAndSourceType(sessionId, sourceType, pageRequest);
        Page<ContextEntryResponseDto> dtoPage = entryPage.map(contextEntryMapper::toResponseDto);
        return PageResponseDto.fromPage(dtoPage);
    }

    @Override
    @Transactional
    public ContextEntryResponseDto createContextEntry(UUID userId, ContextEntryCreateDto entryDto) {
        log.info("Creating context entry for user ID: {} and session ID: {}",
                userId, entryDto.getSessionId());

        byte[] embedding = null;
        if (entryDto.getText() != null && !entryDto.getText().isEmpty()) {
            embedding = embeddingService.generateEmbedding(entryDto.getText());
        }

        ContextEntry entry = ContextEntry.builder()
                .sessionId(entryDto.getSessionId())
                .userId(userId)
                .messageId(entryDto.getMessageId())
                .sourceType(entryDto.getSourceType())
                .content(entryDto.getContent())
                .metadata(entryDto.getMetadata() != null ? entryDto.getMetadata() : new HashMap<>())
                .embedding(embedding)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        ContextEntry savedEntry = contextEntryRepository.save(entry);

        eventProducer.sendContextCreatedEvent(savedEntry.getId(), savedEntry.getSessionId(), userId);

        return contextEntryMapper.toResponseDto(savedEntry);
    }

    @Override
    public ContextEntryResponseDto getContextEntryById(UUID id, UUID userId) {
        log.info("Getting context entry by ID: {} for user ID: {}", id, userId);

        ContextEntry entry = getContextEntryEntityById(id, userId);
        return contextEntryMapper.toResponseDto(entry);
    }

    @Override
    @Transactional
    public ContextEntryResponseDto updateContextEntry(UUID id, UUID userId, ContextEntryUpdateDto entryDto) {
        log.info("Updating context entry ID: {} for user ID: {}", id, userId);

        ContextEntry entry = getContextEntryEntityById(id, userId);
        entry.setContent(entryDto.getContent());

        if (entryDto.getMetadata() != null) {
            entry.setMetadata(entryDto.getMetadata());
        }

        if (entryDto.getText() != null && !entryDto.getText().isEmpty()) {
            entry.setEmbedding(embeddingService.generateEmbedding(entryDto.getText()));
        }

        entry.setUpdatedAt(OffsetDateTime.now());

        ContextEntry updatedEntry = contextEntryRepository.save(entry);

        eventProducer.sendContextUpdatedEvent(updatedEntry.getId(), updatedEntry.getSessionId(), userId);

        return contextEntryMapper.toResponseDto(updatedEntry);
    }

    @Override
    @Transactional
    public void deleteContextEntry(UUID id, UUID userId) {
        log.info("Deleting context entry ID: {} for user ID: {}", id, userId);

        ContextEntry entry = getContextEntryEntityById(id, userId);
        UUID sessionId = entry.getSessionId();

        contextEntryRepository.delete(entry);

        eventProducer.sendContextDeletedEvent(id, sessionId, userId);
    }

    @Override
    public List<ContextEntryResponseDto> semanticSearch(UUID sessionId, UUID userId, String queryText, int limit) {
        log.info("Performing semantic search for session ID: {} and user ID: {}", sessionId, userId);

        // Generate embedding for the query text
        byte[] queryEmbedding = embeddingService.generateEmbedding(queryText);

        // Perform the search using the repository
        List<ContextEntry> results = contextEntryRepository.findSimilarByEmbedding(sessionId, queryEmbedding, limit);

        return results.stream()
                .map(contextEntryMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ContextEntryResponseDto getContextEntryByMessageId(UUID messageId, UUID userId) {
        log.info("Getting context entry by message ID: {} for user ID: {}", messageId, userId);

        ContextEntry entry = contextEntryRepository.findByMessageId(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Context entry not found for message ID: " + messageId));

        if (!entry.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Context entry not found for message ID: " + messageId);
        }

        return contextEntryMapper.toResponseDto(entry);
    }

    private ContextEntry getContextEntryEntityById(UUID id, UUID userId) {
        return contextEntryRepository.findById(id)
                .filter(entry -> entry.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Context entry not found with id: " + id));
    }
}