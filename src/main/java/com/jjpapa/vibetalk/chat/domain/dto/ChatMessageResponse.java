package com.jjpapa.vibetalk.chat.domain.dto;

import com.jjpapa.vibetalk.chat.domain.entity.ChatMessage;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class ChatMessageResponse {
  private Long id;
  private Long senderId;
  private String senderName;
  private String content;
  private LocalDateTime sentAt;

  public static ChatMessageResponse from(ChatMessage message) {
    return new ChatMessageResponse(
        message.getId(),
        message.getSender().getId(),
        message.getSender().getName(),
        message.getContent(),
        message.getSentAt()
    );
  }
}
