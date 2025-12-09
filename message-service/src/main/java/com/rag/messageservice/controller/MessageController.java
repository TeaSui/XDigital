package com.rag.messageservice.controller;

import com.rag.messageservice.dto.MessageCreateDto;
import com.rag.messageservice.dto.MessageResponseDto;
import com.rag.messageservice.dto.MessageUpdateDto;
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
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Message", description = "Message management API")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    @Operation(summary = "Get messages for a session", description = "Returns a page of messages for a session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved messages"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<PageResponseDto<MessageResponseDto>> getMessages(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam UUID sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        log.info("Getting messages for session: {} and user: {}", sessionId, userId);
        return ResponseEntity.ok(messageService.getMessages(sessionId, userId, page, size, sortBy, sortDirection));
    }

    @GetMapping("/session/{sessionId}")
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

    @PostMapping
    @Operation(summary = "Create a new message", description = "Creates a new message for a session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<MessageResponseDto> createMessage(
            @RequestHeader("X-User-ID") UUID userId,
            @Valid @RequestBody MessageCreateDto messageDto) {

        log.info("Creating message for user: {} in session: {}", userId, messageDto.getSessionId());
        MessageResponseDto createdMessage = messageService.createMessage(userId, messageDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a message by ID", description = "Returns a single message by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved message"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Message not found", content = @Content)
    })
    public ResponseEntity<MessageResponseDto> getMessageById(
            @PathVariable UUID id,
            @RequestHeader("X-User-ID") UUID userId) {

        log.info("Getting message {} for user: {}", id, userId);
        return ResponseEntity.ok(messageService.getMessageById(id, userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a message", description = "Updates an existing message by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Message not found", content = @Content)
    })
    public ResponseEntity<MessageResponseDto> updateMessage(
            @PathVariable UUID id,
            @RequestHeader("X-User-ID") UUID userId,
            @Valid @RequestBody MessageUpdateDto messageDto) {

        log.info("Updating message {} for user: {}", id, userId);
        return ResponseEntity.ok(messageService.updateMessage(id, userId, messageDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a message", description = "Deletes a message by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Message not found", content = @Content)
    })
    public ResponseEntity<Void> deleteMessage(
            @PathVariable UUID id,
            @RequestHeader("X-User-ID") UUID userId) {

        log.info("Deleting message {} for user: {}", id, userId);
        messageService.deleteMessage(id, userId);
        return ResponseEntity.noContent().build();
    }
}