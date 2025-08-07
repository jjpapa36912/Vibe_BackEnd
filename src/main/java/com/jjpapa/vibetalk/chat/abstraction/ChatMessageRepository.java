package com.jjpapa.vibetalk.chat.abstraction;

import com.jjpapa.vibetalk.chat.domain.dto.ChatMessageResponse;
import com.jjpapa.vibetalk.chat.domain.entity.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends
    JpaRepository<ChatMessage, Long> {
//  List<ChatMessage> findByRoomId(Long roomId);
  List<ChatMessage> findByChatRoomIdOrderBySentAtAsc(Long chatRoomId);
  // 최신 메시지 50개
  @Query("""
    SELECT new com.jjpapa.vibetalk.chat.domain.dto.ChatMessageResponse(
        m.id,
        s.id,
        s.name,
        m.content,
        m.sentAt,
        m.emotion,
        m.fontName
    )
    FROM ChatMessage m
    JOIN m.sender s
    WHERE m.chatRoom.id = :roomId
    ORDER BY m.sentAt DESC, m.id DESC
""")
  List<ChatMessageResponse> findRecentMessagesDto(
      @Param("roomId") Long roomId,
      Pageable pageable
  );


//  @Query("SELECT new com.jjpapa.vibetalk.chat.domain.dto.ChatMessageResponse(" +
//      "m.id, s.id, s.name, m.content, m.sentAt) " +
//      "FROM ChatMessage m " +
//      "JOIN m.sender s " +
//      "WHERE m.chatRoom.id = :roomId " +
//      "ORDER BY m.sentAt DESC")
//  List<ChatMessageResponse> findRecentMessagesDto(@Param("roomId") Long roomId, Pageable pageable);





//  // 특정 시간 이전의 메시지 (무한 스크롤)
//  @Query("SELECT m FROM ChatMessage m JOIN FETCH m.sender WHERE m.chatRoom.id = :roomId AND m.sentAt < :beforeTime ORDER BY m.sentAt DESC")
//  List<ChatMessage> findOldMessages(@Param("roomId") Long roomId, @Param("beforeTime") LocalDateTime beforeTime, Pageable pageable);
// ✅ 특정 시간 이전의 과거 메시지 조회 (무한 스크롤)
@Query("""
        SELECT new com.jjpapa.vibetalk.chat.domain.dto.ChatMessageResponse(
            m.id,
            s.id,
            s.name,
            m.content,
            m.sentAt
        )
        FROM ChatMessage m
        JOIN m.sender s
        WHERE m.chatRoom.id = :roomId
          AND m.sentAt < :beforeTime
        ORDER BY m.sentAt DESC
    """)
List<ChatMessageResponse> findOldMessagesDto(
    @Param("roomId") Long roomId,
    @Param("beforeTime") LocalDateTime beforeTime,
    Pageable pageable
);


}

