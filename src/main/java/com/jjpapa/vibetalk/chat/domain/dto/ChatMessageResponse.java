package com.jjpapa.vibetalk.chat.domain.dto;

import com.jjpapa.vibetalk.chat.domain.entity.ChatMessage;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageResponse {
  private Long id;                 // 서버 PK
  private String clientMessageId;  // 🔑 클라가 보낸 UUID (nullable 가능)

  private Long senderId;
  private String senderName;
  private String content;
  private LocalDateTime sentAt;
  private String emotion;
  private String fontName;
  private String emoji;

  // 엔티티 → 응답
  public static ChatMessageResponse from(ChatMessage m) {
    return new ChatMessageResponse(
        m.getId(),
        m.getClientMessageId(),     // 🔑 추가
        m.getSender().getId(),
        m.getSender().getName(),
        m.getContent(),
        m.getSentAt(),
        m.getEmotion(),
        m.getFontName(),
        m.getEmoji()
    );
  }

  // ✅ JPQL 프로젝션 표준 생성자 (쿼리 new ... 에서 이 순서로 선택)
  public ChatMessageResponse(
      Long id,
      String clientMessageId,   // 🔑 추가
      Long senderId,
      String senderName,
      String content,
      LocalDateTime sentAt,
      String emotion,
      String fontName,
      String emoji
  ) {
    this.id = id;
    this.clientMessageId = clientMessageId;
    this.senderId = senderId;
    this.senderName = senderName;
    this.content = content;
    this.sentAt = sentAt;
    this.emotion = emotion;
    this.fontName = fontName;
    this.emoji = emoji;
  }
}
