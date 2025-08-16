package com.jjpapa.vibetalk.chat.abstraction;

import com.jjpapa.vibetalk.chat.domain.entity.ChatRoomMember;
import com.jjpapa.vibetalk.login.domain.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomMemberRepository extends
    JpaRepository<ChatRoomMember, Long> {
  List<ChatRoomMember> findByChatRoomId(Long roomId);
  @Query("SELECT m.user FROM ChatRoomMember m WHERE m.chatRoom.id = :roomId")
  List<User> findUsersByRoomId(@Param("roomId") Long roomId);

  boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId);

}
