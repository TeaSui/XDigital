package com.rag.contextservice.service;

/**
 * Service for generating embeddings from text
 */
public interface EmbeddingService {

    /**
     * Generates an embedding from text
     *
     * @param text the text to generate an embedding for
     * @return the embedding as a byte array
     */
    byte[] generateEmbedding(String text);
}