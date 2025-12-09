package com.rag.sessionservice.service.impl;

import com.rag.sessionservice.dto.PageResponseDto;
import com.rag.sessionservice.dto.SessionCreateDto;
import com.rag.sessionservice.dto.SessionResponseDto;
import com.rag.sessionservice.dto.SessionUpdateDto;
import com.rag.sessionservice.event.SessionEventProducer;
import com.ragchat.common.event.SessionEventType;
import com.rag.sessionservice.exception.ResourceNotFoundException;
import com.rag.sessionservice.mapper.SessionMapper;
import com.rag.sessionservice.model.Session;
import com.rag.sessionservice.repository.SessionRepository;
import com.rag.sessionservice.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final SessionEventProducer eventProducer;

    @Override
    public PageResponseDto<SessionResponseDto> getSessions(
            UUID userId, int page, int size, Boolean isFavorite,
            String sortBy, String sortDirection) {

        log.info("Getting sessions for user ID: {}", userId);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Session> sessionPage;
        if (isFavorite != null) {
            sessionPage = sessionRepository.findByUserIdAndFavorite(userId, isFavorite, pageRequest);
        } else {
            sessionPage = sessionRepository.findByUserId(userId, pageRequest);
        }

        Page<SessionResponseDto> dtoPage = sessionPage.map(sessionMapper::toResponseDto);
        return PageResponseDto.fromPage(dtoPage);
    }

    @Override
    @Transactional
    public SessionResponseDto createSession(UUID userId, SessionCreateDto sessionDto) {
        log.info("Creating session for user ID: {}", userId);

        Session session = new Session();
        session.setUserId(userId);
        session.setName(sessionDto.getName());
        session.setFavorite(false);
        session.setMessageCount(0);

        Session savedSession = sessionRepository.save(session);

        eventProducer.sendSessionCreatedEvent(savedSession.getId(), userId);

        return sessionMapper.toResponseDto(savedSession);
    }

    @Override
    public SessionResponseDto getSessionById(UUID id, UUID userId) {
        log.info("Getting session by ID: {} for user ID: {}", id, userId);

        Session session = getSessionEntityByIdAndUserId(id, userId);
        return sessionMapper.toResponseDto(session);
    }

    @Override
    @Transactional
    public SessionResponseDto updateSession(UUID id, UUID userId, SessionUpdateDto sessionDto) {
        log.info("Updating session ID: {} for user ID: {}", id, userId);

        Session session = getSessionEntityByIdAndUserId(id, userId);

        if (sessionDto.getName() != null) {
            session.setName(sessionDto.getName());
        }

        if (sessionDto.getIsFavorite() != null) {
            session.setFavorite(sessionDto.getIsFavorite());
        }

        session.setUpdatedAt(OffsetDateTime.now());
        Session updatedSession = sessionRepository.save(session);

        eventProducer.sendSessionUpdatedEvent(updatedSession.getId(), userId);

        return sessionMapper.toResponseDto(updatedSession);
    }

    @Override
    @Transactional
    public void deleteSession(UUID id, UUID userId) {
        log.info("Deleting session ID: {} for user ID: {}", id, userId);

        Session session = getSessionEntityByIdAndUserId(id, userId);
        sessionRepository.delete(session);

        eventProducer.sendSessionDeletedEvent(id, userId);
    }

    @Override
    @Transactional
    public SessionResponseDto incrementMessageCount(UUID sessionId, OffsetDateTime timestamp) {
        log.info("Incrementing message count for session ID: {}", sessionId);

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + sessionId));

        session.setMessageCount(session.getMessageCount() + 1);
        session.setLastMessageAt(timestamp);
        session.setUpdatedAt(OffsetDateTime.now());

        Session updatedSession = sessionRepository.save(session);
        return sessionMapper.toResponseDto(updatedSession);
    }

    @Override
    public boolean existsSessionByIdAndUserId(UUID id, UUID userId) {
        return sessionRepository.existsByIdAndUserId(id, userId);
    }

    private Session getSessionEntityByIdAndUserId(UUID id, UUID userId) {
        return sessionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + id));
    }

}