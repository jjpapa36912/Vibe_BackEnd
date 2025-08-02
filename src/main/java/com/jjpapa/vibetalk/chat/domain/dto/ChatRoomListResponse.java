package com.jjpapa.vibetalk.chat.domain.dto;


import com.jjpapa.vibetalk.chat.domain.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomListResponse {
  private Long id;
  private String roomName;

  public static ChatRoomListResponse from(ChatRoom room) {
    return new ChatRoomListResponse(room.getId(), room.getRoomName());
  }
}