package com.rag.contextservice.service.impl;

import com.rag.contextservice.service.EmbeddingService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Dummy implementation of EmbeddingService that generates pseudo-embeddings
 * In a production environment, this would be replaced with a real embedding service
 * that calls an embedding model API (e.g., OpenAI, HuggingFace, etc.)
 */
@Service
public class DummyEmbeddingServiceImpl implements EmbeddingService {

    @Override
    public byte[] generateEmbedding(String text) {
        if (text == null || text.isEmpty()) {
            // Return an empty embedding for empty text
            return new byte[32];
        }

        try {
            // Use SHA-256 to generate a pseudo-embedding (32 bytes)
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(text.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate embedding", e);
        }
    }
}