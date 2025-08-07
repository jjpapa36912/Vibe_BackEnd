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

  // ✅ 추가
  private String emotion;

  // ✅ 추가
  private String fontName;

  public static ChatMessageDto fromEntity(ChatMessage message) {
    return new ChatMessageDto(
        message.getId(),
        message.getSender().getId(),
        message.getSender().getName(),
        message.getContent(),
        message.getSentAt().toString(),
        message.getChatRoom().getId(),
        message.getEmotion(),     // ✅ 추가
        message.getFontName()     // ✅ 추가
    );
  }
}

//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class ChatMessageDto {
//  private Long id;
//  private Long senderId;
//  private String senderName;
//  private String content;
//  private String sentAt;
//  private Long chatRoomId;
//
//  public static ChatMessageDto fromEntity(ChatMessage message) {
//    return new ChatMessageDto(
//        message.getId(),
//        message.getSender().getId(),
//        message.getSender().getName(),
//        message.getContent(),
//        message.getSentAt().toString(),
//        message.getChatRoom().getId()
//    );
//  }
//}

//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class ChatMessageDto {
//  private Long chatRoomId;
//  private Long senderId;
//  private String content;
//}

