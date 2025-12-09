package com.rag.sessionservice.controller;

import com.rag.sessionservice.dto.PageResponseDto;
import com.rag.sessionservice.dto.SessionCreateDto;
import com.rag.sessionservice.dto.SessionResponseDto;
import com.rag.sessionservice.dto.SessionUpdateDto;
import com.rag.sessionservice.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Session", description = "Session management API")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    @Operation(summary = "Get all sessions", description = "Returns a page of sessions for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved sessions"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<PageResponseDto<SessionResponseDto>> getSessions(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean isFavorite,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        log.info("Getting sessions for user: {}", userId);
        return ResponseEntity.ok(sessionService.getSessions(userId, page, size, isFavorite, sortBy, sortDirection));
    }

    @PostMapping
    @Operation(summary = "Create a new session", description = "Creates a new session for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Session created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<SessionResponseDto> createSession(
            @RequestHeader("X-User-ID") UUID userId,
            @Valid @RequestBody SessionCreateDto sessionDto) {

        log.info("Creating session for user: {}", userId);
        SessionResponseDto createdSession = sessionService.createSession(userId, sessionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSession);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a session by ID", description = "Returns a single session by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved session"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content)
    })
    public ResponseEntity<SessionResponseDto> getSessionById(
            @PathVariable UUID id,
            @RequestHeader("X-User-ID") UUID userId) {

        log.info("Getting session {} for user: {}", id, userId);
        return ResponseEntity.ok(sessionService.getSessionById(id, userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a session", description = "Updates an existing session by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content)
    })
    public ResponseEntity<SessionResponseDto> updateSession(
            @PathVariable UUID id,
            @RequestHeader("X-User-ID") UUID userId,
            @Valid @RequestBody SessionUpdateDto sessionDto) {

        log.info("Updating session {} for user: {}", id, userId);
        return ResponseEntity.ok(sessionService.updateSession(id, userId, sessionDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a session", description = "Deletes a session by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Session deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content)
    })
    public ResponseEntity<Void> deleteSession(
            @PathVariable UUID id,
            @RequestHeader("X-User-ID") UUID userId) {

        log.info("Deleting session {} for user: {}", id, userId);
        sessionService.deleteSession(id, userId);
        return ResponseEntity.noContent().build();
    }
}