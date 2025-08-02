package com.jjpapa.vibetalk.chat.domain.dto;

import com.jjpapa.vibetalk.chat.domain.entity.ChatRoom;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponse {
  private Long id;
  private String roomName;
  private Long createdBy;
  private LocalDateTime createdAt;

  public ChatRoomResponse(ChatRoom room) {
    this.id = room.getId();
    this.roomName = room.getRoomName();
  }

  public ChatRoomResponse(Long id, String roomName) {
    this.id = id;
    this.roomName = roomName;
  }
}
