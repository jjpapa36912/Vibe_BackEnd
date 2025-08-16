package com.jjpapa.vibetalk.chat.domain.dto;

import com.jjpapa.vibetalk.chat.domain.entity.ChatRoom;
import com.jjpapa.vibetalk.chat.domain.enumeration.ChatRoomMode;
import com.jjpapa.vibetalk.login.domain.entity.User;
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
  private ChatRoomMode mode;
  private Long createdBy;
  private LocalDateTime createdAt;

  /** 엔티티 → DTO (기본 변환) */
  public static ChatRoomResponse from(ChatRoom room) {
    User creator = room.getCreator();
    return ChatRoomResponse.builder()
        .id(room.getId())
        .roomName(room.getRoomName())
        .mode(room.getMode())
        .createdBy(creator != null ? creator.getId() : null)
        .createdAt(room.getCreatedAt())
        .build();
  }

  /** 목록용: 표시 이름(디스플레이 네임)만 바꿔서 내보낼 때 */
  public static ChatRoomResponse from(ChatRoom room, String displayName) {
    User creator = room.getCreator();
    return ChatRoomResponse.builder()
        .id(room.getId())
        .roomName(displayName)                           // 👈 override
        .mode(room.getMode())
        .createdBy(creator != null ? creator.getId() : null)
        .createdAt(room.getCreatedAt())
        .build();
  }

  /** 필요 시: 필드 직접 지정해 만들 때 */
  public static ChatRoomResponse of(
      Long id, String roomName, ChatRoomMode mode, Long createdBy, LocalDateTime createdAt) {
    return ChatRoomResponse.builder()
        .id(id).roomName(roomName).mode(mode).createdBy(createdBy).createdAt(createdAt)
        .build();
  }

}