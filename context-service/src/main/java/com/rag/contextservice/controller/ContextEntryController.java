package com.rag.contextservice.controller;

import com.rag.contextservice.dto.ContextEntryCreateDto;
import com.rag.contextservice.dto.ContextEntryResponseDto;
import com.rag.contextservice.dto.ContextEntryUpdateDto;
import com.rag.contextservice.dto.PageResponseDto;
import com.rag.contextservice.dto.SemanticSearchRequestDto;
import com.rag.contextservice.model.SourceType;
import com.rag.contextservice.service.ContextEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contexts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Context", description = "Context entry management API")
public class ContextEntryController {

    private final ContextEntryService contextEntryService;

    @GetMapping
    @Operation(summary = "Get context entries", description = "Returns a page of context entries for a session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved context entries"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<PageResponseDto<ContextEntryResponseDto>> getContextEntries(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam UUID sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        log.info("Getting context entries for session: {} and user: {}", sessionId, userId);
        return ResponseEntity.ok(
                contextEntryService.getContextEntries(sessionId, userId, page, size, sortBy, sortDirection));
    }

    @GetMapping("/source-type")
    @Operation(summary = "Get context entries by source type", description = "Returns a page of context entries for a session filtered by source type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved context entries"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<PageResponseDto<ContextEntryResponseDto>> getContextEntriesBySourceType(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam UUID sessionId,
            @RequestParam SourceType sourceType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        log.info("Getting context entries for session: {}, user: {}, and source type: {}",
                sessionId, userId, sourceType);
        return ResponseEntity.ok(
                contextEntryService.getContextEntriesBySourceType(
                        sessionId, userId, sourceType, page, size, sortBy, sortDirection));
    }

    @PostMapping
    @Operation(summary = "Create a new context entry", description = "Creates a new context entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Context entry created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<ContextEntryResponseDto> createContextEntry(
            @RequestHeader("X-User-ID") UUID userId,
            @Valid @RequestBody ContextEntryCreateDto entryDto) {

        log.info("Creating context entry for user: {} in session: {}", userId, entryDto.getSessionId());
        ContextEntryResponseDto createdEntry = contextEntryService.createContextEntry(userId, entryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntry);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a context entry by ID", description = "Returns a single context entry by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved context entry"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Context entry not found", content = @Content)
    })
    public ResponseEntity<ContextEntryResponseDto> getContextEntryById(
            @PathVariable UUID id,
            @RequestHeader("X-User-ID") UUID userId) {

        log.info("Getting context entry {} for user: {}", id, userId);
        return ResponseEntity.ok(contextEntryService.getContextEntryById(id, userId));
    }

    @GetMapping("/message/{messageId}")
    @Operation(summary = "Get a context entry by message ID", description = "Returns a single context entry by its associated message ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved context entry"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Context entry not found", content = @Content)
    })
    public ResponseEntity<ContextEntryResponseDto> getContextEntryByMessageId(
            @PathVariable UUID messageId,
            @RequestHeader("X-User-ID") UUID userId) {

        log.info("Getting context entry for message ID: {} and user: {}", messageId, userId);
        return ResponseEntity.ok(contextEntryService.getContextEntryByMessageId(messageId, userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a context entry", description = "Updates an existing context entry by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Context entry updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Context entry not found", content = @Content)
    })
    public ResponseEntity<ContextEntryResponseDto> updateContextEntry(
            @PathVariable UUID id,
            @RequestHeader("X-User-ID") UUID userId,
            @Valid @RequestBody ContextEntryUpdateDto entryDto) {

        log.info("Updating context entry {} for user: {}", id, userId);
        return ResponseEntity.ok(contextEntryService.updateContextEntry(id, userId, entryDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a context entry", description = "Deletes a context entry by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Context entry deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Context entry not found", content = @Content)
    })
    public ResponseEntity<Void> deleteContextEntry(
            @PathVariable UUID id,
            @RequestHeader("X-User-ID") UUID userId) {

        log.info("Deleting context entry {} for user: {}", id, userId);
        contextEntryService.deleteContextEntry(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    @Operation(summary = "Semantic search", description = "Performs a semantic search for context entries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved search results"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<List<ContextEntryResponseDto>> semanticSearch(
            @RequestHeader("X-User-ID") UUID userId,
            @Valid @RequestBody SemanticSearchRequestDto searchRequest) {

        log.info("Performing semantic search for user: {} and session: {}",
                userId, searchRequest.getSessionId());
        return ResponseEntity.ok(contextEntryService.semanticSearch(
                searchRequest.getSessionId(), userId, searchRequest.getQueryText(), searchRequest.getLimit()));
    }
}