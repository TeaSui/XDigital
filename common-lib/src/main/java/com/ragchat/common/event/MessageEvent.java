package com.ragchat.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageEvent {
    private MessageEventType type;
    private UUID messageId;
    private UUID sessionId;
    private UUID userId;
    private Instant timestamp;
}