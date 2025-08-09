package com.jjpapa.vibetalk.chat.abstraction;

import com.jjpapa.vibetalk.chat.domain.dto.ChatMessageResponse;
import com.jjpapa.vibetalk.chat.domain.entity.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends
    JpaRepository<ChatMessage, Long> {
  Optional<ChatMessage> findByClientMessageId(String clientMessageId);
  Optional<ChatMessage> findByClientMessageIdAndSenderIdAndChatRoomId(
      String clientMessageId, Long senderId, Long chatRoomId);
//  List<ChatMessage> findByRoomId(Long roomId);
  List<ChatMessage> findByChatRoomIdOrderBySentAtAsc(Long chatRoomId);
  // 최신 메시지 50개
  // import 생략

    @Query("""
     select new com.jjpapa.vibetalk.chat.domain.dto.ChatMessageResponse(
        m.id,
            m.clientMessageId,                
        
        m.sender.id,
        m.sender.name,
        m.content,
        m.sentAt,
        m.emotion,        
        m.fontName,       
        m.emoji          
     )
     from ChatMessage m
     where m.chatRoom.id = :roomId
     order by m.sentAt desc
  """)
    List<ChatMessageResponse> findRecentMessagesDto(@Param("roomId") Long roomId, Pageable pageable);

    @Query("""
     select new com.jjpapa.vibetalk.chat.domain.dto.ChatMessageResponse(
        m.id,
         m.clientMessageId, 
        m.sender.id,
        m.sender.name,
        m.content,
        m.sentAt,
        m.emotion,
        m.fontName,
        m.emoji
     )
     from ChatMessage m
     where m.chatRoom.id = :roomId
       and m.sentAt < :before
     order by m.sentAt desc
  """)
    List<ChatMessageResponse> findOldMessagesDto(@Param("roomId") Long roomId,
        @Param("before") LocalDateTime before,
        Pageable pageable);
  }




