package com.jjpapa.vibetalk.chat.domain.dto;

import com.jjpapa.vibetalk.chat.domain.enumeration.ChatRoomMode;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateChatRoomRequest {
  private List<Long> memberIds;  // 초대할 유저 ID들 (7명까지)
  private String roomName;
  private ChatRoomMode mode; // ✅ 추가 (null 가능)


}

