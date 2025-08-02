package com.jjpapa.vibetalk.chat.domain.entity;

import com.jjpapa.vibetalk.login.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ChatRoomMember {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chat_room_id")
  private ChatRoom chatRoom;

  private LocalDateTime joinedAt;
}

