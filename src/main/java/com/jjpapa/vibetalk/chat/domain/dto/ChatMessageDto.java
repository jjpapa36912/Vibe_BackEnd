package com.jjpapa.vibetalk.chat.domain.dto;

import com.jjpapa.vibetalk.chat.domain.entity.ChatMessage;
import com.jjpapa.vibetalk.chat.domain.entity.ChatRoom;
import com.jjpapa.vibetalk.login.domain.entity.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
  private Long id;
  private Long senderId;
  private String senderName;
  private String content;
  private String sentAt;
  private Long chatRoomId;

  private String emotion;    // ✅ 감정
  private String fontName;   // ✅ 폰트
  private String emoji;      // ✅ 이모지 ← 이거 추가 필요!!

  public static ChatMessageDto fromEntity(ChatMessage message) {
    return new ChatMessageDto(
        message.getId(),
        message.getSender().getId(),
        message.getSender().getName(),
        message.getContent(),
        message.getSentAt().toString(),
        message.getChatRoom().getId(),
        message.getEmotion(),
        message.getFontName(),
        message.getEmoji()     // ✅ 이모지도 포함
    );
  }
}

