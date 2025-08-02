package com.jjpapa.vibetalk.chat.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessage {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long roomId;
  private Long senderId;
  private String senderName;
  private String message;
  private LocalDateTime createdAt = LocalDateTime.now();
}
