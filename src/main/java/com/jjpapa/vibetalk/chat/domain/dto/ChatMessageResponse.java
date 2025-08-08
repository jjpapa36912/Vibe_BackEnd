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

  private String emotion;   // ✅ 감정 결과
  private String fontName;  // ✅ 폰트
  private String emoji;     // ✅ 이모지 (💡 추가된 부분)

  // ChatMessage → ChatMessageResponse 변환용 팩토리 메서드
  public static ChatMessageResponse from(ChatMessage message) {
    return new ChatMessageResponse(
        message.getId(),
        message.getSender().getId(),
        message.getSender().getName(),
        message.getContent(),
        message.getSentAt(),
        message.getEmotion(),
        message.getFontName(),
        message.getEmoji()     // ✅ 전달
    );
  }

  // JPQL Projection용 생성자
  public ChatMessageResponse(Long id, Long senderId, String senderName, String content, LocalDateTime sentAt) {
    this.id = id;
    this.senderId = senderId;
    this.senderName = senderName;
    this.content = content;
    this.sentAt = sentAt;
    // emotion, fontName, emoji는 null로 유지됨 (JPQL에서 조회 안할 경우 대비)
  }

  public ChatMessageResponse(Long messageId, Long senderId, String senderName, String content,
      LocalDateTime sentAt, String emoji, String fontName) {
    this.id = messageId;
    this.senderId = senderId;
    this.senderName = senderName;
    this.content = content;
    this.sentAt = sentAt;
    this.emoji = emoji;
    this.fontName = fontName;
  }
  public ChatMessageResponse(Long messageId, Long senderId, String senderName, String content,
      LocalDateTime sentAt, String emotion, String fontName, String emoji) {
    this.id = messageId;
    this.senderId = senderId;
    this.senderName = senderName;
    this.content = content;
    this.sentAt = sentAt;
    this.emotion = emotion;
    this.fontName = fontName;
    this.emoji = emoji;
  }


}