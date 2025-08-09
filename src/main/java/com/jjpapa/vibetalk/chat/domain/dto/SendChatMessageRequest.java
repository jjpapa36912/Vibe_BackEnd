package com.jjpapa.vibetalk.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder

// SendChatMessageRequest.java
public record SendChatMessageRequest(
    Long chatRoomId,
    Long senderId,
    String content,
    String clientMessageId,  // 🔑
    String sentAt,           // ISO-8601 문자열(옵션)
    String emotion,          // 옵션
    String fontName,         // 옵션
    String emoji             // 옵션
) {}
