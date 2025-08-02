package com.jjpapa.vibetalk.chat.abstraction;

import com.jjpapa.vibetalk.chat.domain.entity.ChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends
    JpaRepository<ChatMessage, Long> {
  List<ChatMessage> findByRoomId(Long roomId);

}

