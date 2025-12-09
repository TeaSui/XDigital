package com.rag.sessionservice.repository;

import com.rag.sessionservice.model.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    Page<Session> findByUserId(UUID userId, Pageable pageable);
    Page<Session> findByUserIdAndFavorite(UUID userId, boolean favorite, Pageable pageable);
    Optional<Session> findByIdAndUserId(UUID id, UUID userId);
    boolean existsByIdAndUserId(UUID id, UUID userId);
}