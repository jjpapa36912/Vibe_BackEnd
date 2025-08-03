package com.jjpapa.vibetalk.chat.domain.dto;

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
  private Long chatRoomId;
  private Long senderId;
  private String content;
}

