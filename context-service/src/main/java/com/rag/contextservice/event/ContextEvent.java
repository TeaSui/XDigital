package com.rag.contextservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContextEvent {
    private ContextEventType type;
    private UUID contextEntryId;
    private UUID sessionId;
    private UUID userId;
    private OffsetDateTime timestamp;
}