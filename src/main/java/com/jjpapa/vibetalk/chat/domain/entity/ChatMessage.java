package com.jjpapa.vibetalk.chat.domain.entity;

import com.jjpapa.vibetalk.login.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chat_room_id")
  private ChatRoom chatRoom;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id")
  private User sender;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "font_name")
  private String fontName; // ✅ 추가
  // ✅ 추가
  private String emotion;


  @Column(nullable = false)
  private LocalDateTime sentAt;
  @Builder
  public ChatMessage(ChatRoom chatRoom, User sender, String content, LocalDateTime sentAt, String emotion, String fontName) {
    this.chatRoom = chatRoom;
    this.sender = sender;
    this.content = content;
    this.sentAt = sentAt;
    this.emotion = emotion;
    this.fontName = fontName;
  }
}
