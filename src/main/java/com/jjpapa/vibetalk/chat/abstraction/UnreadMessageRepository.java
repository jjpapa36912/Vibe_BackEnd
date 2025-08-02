package com.jjpapa.vibetalk.chat.abstraction;

import com.jjpapa.vibetalk.chat.domain.entity.UnreadMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnreadMessageRepository extends
    JpaRepository<UnreadMessage, Long> {
  int countByUserId(Long userId);
  void deleteByUserIdAndRoomId(Long userId, Long roomId);
}
