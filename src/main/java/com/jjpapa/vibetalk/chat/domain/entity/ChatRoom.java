package com.jjpapa.vibetalk.chat.domain.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ChatRoom {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String roomName;

  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
  private List<ChatRoomMember> members;
}

