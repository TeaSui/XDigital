package com.rag.messageservice.service.impl;

import com.rag.messageservice.dto.MessageCreateDto;
import com.rag.messageservice.dto.MessageResponseDto;
import com.rag.messageservice.dto.MessageUpdateDto;
import com.rag.messageservice.dto.PageResponseDto;
import com.rag.messageservice.event.MessageEventProducer;
import com.rag.messageservice.exception.ResourceNotFoundException;
import com.rag.messageservice.mapper.MessageMapper;
import com.rag.messageservice.model.Message;
import com.rag.messageservice.repository.MessageRepository;
import com.rag.messageservice.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final MessageEventProducer eventProducer;

    @Override
    public PageResponseDto<MessageResponseDto> getMessages(
            UUID sessionId, UUID userId, int page, int size, String sortBy, String sortDirection) {

        log.info("Getting messages for session ID: {} and user ID: {}", sessionId, userId);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Message> messagePage = messageRepository.findBySessionIdAndUserId(sessionId, userId, pageRequest);
        Page<MessageResponseDto> dtoPage = messagePage.map(messageMapper::toResponseDto);
        return PageResponseDto.fromPage(dtoPage);
    }

    @Override
    public List<MessageResponseDto> getAllSessionMessages(UUID sessionId, UUID userId) {
        log.info("Getting all messages for session ID: {} and user ID: {}", sessionId, userId);

        List<Message> messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        return messages.stream()
                .map(messageMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MessageResponseDto createMessage(UUID userId, MessageCreateDto messageDto) {
        log.info("Creating message for user ID: {} and session ID: {}", userId, messageDto.getSessionId());

        Message message = Message.builder()
                .sessionId(messageDto.getSessionId())
                .userId(userId)
                .role(messageDto.getRole())
                .content(messageDto.getContent())
                .tokenCount(countTokens(messageDto.getContent()))
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        Message savedMessage = messageRepository.save(message);

        eventProducer.sendMessageCreatedEvent(savedMessage.getId(), savedMessage.getSessionId(), userId);

        return messageMapper.toResponseDto(savedMessage);
    }

    @Override
    public MessageResponseDto getMessageById(UUID id, UUID userId) {
        log.info("Getting message by ID: {} for user ID: {}", id, userId);

        Message message = getMessageEntityById(id, userId);
        return messageMapper.toResponseDto(message);
    }

    @Override
    @Transactional
    public MessageResponseDto updateMessage(UUID id, UUID userId, MessageUpdateDto messageDto) {
        log.info("Updating message ID: {} for user ID: {}", id, userId);

        Message message = getMessageEntityById(id, userId);
        message.setContent(messageDto.getContent());
        message.setTokenCount(countTokens(messageDto.getContent()));
        message.setUpdatedAt(OffsetDateTime.now());

        Message updatedMessage = messageRepository.save(message);

        eventProducer.sendMessageUpdatedEvent(updatedMessage.getId(), updatedMessage.getSessionId(), userId);

        return messageMapper.toResponseDto(updatedMessage);
    }

    @Override
    @Transactional
    public void deleteMessage(UUID id, UUID userId) {
        log.info("Deleting message ID: {} for user ID: {}", id, userId);

        Message message = getMessageEntityById(id, userId);
        UUID sessionId = message.getSessionId();

        messageRepository.delete(message);

        eventProducer.sendMessageDeletedEvent(id, sessionId, userId);
    }

    @Override
    public int countTokens(String content) {
        // This is a simple approximation of token counting
        // In a real implementation, you would use a proper tokenizer for your LLM
        if (content == null || content.isEmpty()) {
            return 0;
        }

        // Rough estimate: 4 characters per token on average
        return content.length() / 4 + 1;
    }

    private Message getMessageEntityById(UUID id, UUID userId) {
        return messageRepository.findById(id)
                .filter(m -> m.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
    }
}