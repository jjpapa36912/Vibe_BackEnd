package com.jjpapa.vibetalk.chat.domain.dto;

public record UpdateMessageRequest(
    String clientMessageId,
    Long senderId,
    String emotion,
    String fontName,
    String emoji
) {}
