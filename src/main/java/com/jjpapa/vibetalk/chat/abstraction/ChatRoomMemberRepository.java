package com.jjpapa.vibetalk.chat.abstraction;

import com.jjpapa.vibetalk.chat.domain.entity.ChatRoomMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomMemberRepository extends
    JpaRepository<ChatRoomMember, Long> {
  List<ChatRoomMember> findByChatRoomId(Long roomId);


}
