package com.jjpapa.vibetalk.chat.domain.dto;

import com.jjpapa.vibetalk.chat.domain.entity.ChatMessage;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class ChatMessageResponse {
  private Long id;
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
        m.getSender().getId(),
        m.getSender().getName(),
        m.getContent(),
        m.getSentAt(),
        m.getEmotion(),
        m.getFontName(),
        m.getEmoji()
    );
  }

  // ✅ JPQL 프로젝션 표준 생성자 (이것만 쓰는 걸 추천)
  public ChatMessageResponse(Long id, Long senderId, String senderName, String content,
      LocalDateTime sentAt, String emotion, String fontName, String emoji) {
    this.id = id;
    this.senderId = senderId;
    this.senderName = senderName;
    this.content = content;
    this.sentAt = sentAt;
    this.emotion = emotion;
    this.fontName = fontName;
    this.emoji = emoji;
  }
}
