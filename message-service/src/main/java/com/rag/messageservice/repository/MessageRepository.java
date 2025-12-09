package com.rag.messageservice.repository;

import com.rag.messageservice.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    /**
     * Find messages by session ID
     *
     * @param sessionId the session ID
     * @param pageable  the pagination information
     * @return page of messages
     */
    Page<Message> findBySessionId(UUID sessionId, Pageable pageable);

    /**
     * Find messages by session ID and user ID
     *
     * @param sessionId the session ID
     * @param userId    the user ID
     * @param pageable  the pagination information
     * @return page of messages
     */
    Page<Message> findBySessionIdAndUserId(UUID sessionId, UUID userId, Pageable pageable);

    /**
     * Find all messages by session ID ordered by creation time
     *
     * @param sessionId the session ID
     * @return list of messages
     */
    List<Message> findBySessionIdOrderByCreatedAtAsc(UUID sessionId);

    /**
     * Count messages by session ID
     *
     * @param sessionId the session ID
     * @return count of messages
     */
    long countBySessionId(UUID sessionId);

    /**
     * Delete messages by session ID
     *
     * @param sessionId the session ID
     */
    void deleteBySessionId(UUID sessionId);
}