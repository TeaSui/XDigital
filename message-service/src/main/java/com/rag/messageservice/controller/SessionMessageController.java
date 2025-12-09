package com.rag.messageservice.controller;

import com.rag.messageservice.dto.MessageCreateDto;
import com.rag.messageservice.dto.MessageResponseDto;
import com.rag.messageservice.dto.PageResponseDto;
import com.rag.messageservice.service.MessageService;
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
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Session Messages", description = "Session messages API")
public class SessionMessageController {

    private final MessageService messageService;

    @GetMapping("/{sessionId}/messages")
    @Operation(summary = "Get messages for a session", description = "Returns a page of messages for a session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved messages"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<PageResponseDto<MessageResponseDto>> getSessionMessages(
            @PathVariable UUID sessionId,
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        log.info("Getting messages for session: {} and user: {}", sessionId, userId);
        return ResponseEntity.ok(messageService.getMessages(sessionId, userId, page, size, sortBy, sortDirection));
    }

    @GetMapping("/{sessionId}/messages/all")
    @Operation(summary = "Get all messages for a session", description = "Returns all messages for a session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved messages"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<List<MessageResponseDto>> getAllSessionMessages(
            @PathVariable UUID sessionId,
            @RequestHeader("X-User-ID") UUID userId) {

        log.info("Getting all messages for session: {} and user: {}", sessionId, userId);
        return ResponseEntity.ok(messageService.getAllSessionMessages(sessionId, userId));
    }

    @PostMapping("/{sessionId}/messages")
    @Operation(summary = "Create a new message", description = "Creates a new message for a session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<MessageResponseDto> createSessionMessage(
            @PathVariable UUID sessionId,
            @RequestHeader("X-User-ID") UUID userId,
            @Valid @RequestBody MessageCreateDto messageDto) {

        // Ensure the sessionId in the path matches the one in the request body
        messageDto.setSessionId(sessionId);

        log.info("Creating message for user: {} in session: {}", userId, sessionId);
        MessageResponseDto createdMessage = messageService.createMessage(userId, messageDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }
}