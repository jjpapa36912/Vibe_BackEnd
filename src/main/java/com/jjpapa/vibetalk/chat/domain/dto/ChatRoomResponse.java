package com.jjpapa.vibetalk.chat.domain.dto;

import com.jjpapa.vibetalk.chat.domain.entity.ChatRoom;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomResponse {
  private Long id;
  private String roomName;
  private Long createdBy;
  private LocalDateTime createdAt;
  public static ChatRoomResponse from(ChatRoom chatRoom) {
    return ChatRoomResponse.builder()
        .id(chatRoom.getId())
        .roomName(chatRoom.getRoomName())
        .build();
  }
  public ChatRoomResponse(ChatRoom room) {
    this.id = room.getId();
    this.roomName = room.getRoomName();
  }

  public ChatRoomResponse(Long id, String roomName) {
    this.id = id;
    this.roomName = roomName;
  }
}
