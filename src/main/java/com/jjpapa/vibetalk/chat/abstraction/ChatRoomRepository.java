package com.jjpapa.vibetalk.chat.abstraction;


import com.jjpapa.vibetalk.chat.domain.entity.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
  // ✅ ChatRoomMember 조인해서 특정 userId가 포함된 방을 찾음
  @Query("SELECT DISTINCT crm.chatRoom " +
      "FROM ChatRoomMember crm " +
      "WHERE crm.user.id = :userId")
  List<ChatRoom> findByUserId(@Param("userId") Long userId);
  @Query("SELECT r FROM ChatRoom r JOIN ChatRoomMember m ON r.id = m.chatRoom.id WHERE m.user.id = :userId")
  List<ChatRoom> findAllByMember(@Param("userId") Long userId);
  @Query("SELECT r.id FROM ChatRoom r " +
      "JOIN ChatRoomMember m ON r.id = m.chatRoom.id " +
      "WHERE m.user.id IN :memberIds " +
      "GROUP BY r.id " +
      "HAVING COUNT(DISTINCT m.user.id) = :memberCount " +
      "AND COUNT(DISTINCT m.user.id) = " +
      "(SELECT COUNT(m2) FROM ChatRoomMember m2 WHERE m2.chatRoom.id = r.id)")
  List<Long> findRoomWithExactMembers(@Param("memberIds") List<Long> memberIds,
      @Param("memberCount") long memberCount);

}

