package com.rag.contextservice.repository;

import com.rag.contextservice.model.ContextEntry;
import com.rag.contextservice.model.SourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContextEntryRepository extends JpaRepository<ContextEntry, UUID> {

    /**
     * Find context entries by session ID
     *
     * @param sessionId the session ID
     * @param pageable  the pagination information
     * @return page of context entries
     */
    Page<ContextEntry> findBySessionId(UUID sessionId, Pageable pageable);

    /**
     * Find context entries by session ID and user ID
     *
     * @param sessionId the session ID
     * @param userId    the user ID
     * @param pageable  the pagination information
     * @return page of context entries
     */
    Page<ContextEntry> findBySessionIdAndUserId(UUID sessionId, UUID userId, Pageable pageable);

    /**
     * Find context entries by session ID and source type
     *
     * @param sessionId  the session ID
     * @param sourceType the source type
     * @param pageable   the pagination information
     * @return page of context entries
     */
    Page<ContextEntry> findBySessionIdAndSourceType(UUID sessionId, SourceType sourceType, Pageable pageable);

    /**
     * Find context entry by message ID
     *
     * @param messageId the message ID
     * @return the context entry
     */
    Optional<ContextEntry> findByMessageId(UUID messageId);

    /**
     * Find all context entries by session ID ordered by creation time
     *
     * @param sessionId the session ID
     * @return list of context entries
     */
    List<ContextEntry> findBySessionIdOrderByCreatedAtDesc(UUID sessionId);

    /**
     * Delete context entries by session ID
     *
     * @param sessionId the session ID
     */
    void deleteBySessionId(UUID sessionId);

    /**
     * Delete context entries by message ID
     *
     * @param messageId the message ID
     */
    void deleteByMessageId(UUID messageId);

    /**
     * Semantic search over context entries
     *
     * @param sessionId the session ID
     * @param embedding the query embedding
     * @param limit the number of results to return
     * @return list of context entries
     */
    @Query(nativeQuery = true, value =
            "SELECT * FROM context_entries " +
                    "WHERE session_id = ?1 " +
                    "ORDER BY embedding <-> ?2 " +
                    "LIMIT ?3")
    List<ContextEntry> findSimilarByEmbedding(UUID sessionId, byte[] embedding, int limit);
}