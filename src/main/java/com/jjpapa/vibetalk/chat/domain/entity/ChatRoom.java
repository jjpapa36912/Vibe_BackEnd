package com.jjpapa.vibetalk.chat.domain.entity;


import com.jjpapa.vibetalk.chat.domain.enumeration.ChatRoomMode;
import com.jjpapa.vibetalk.login.domain.entity.User;
import jakarta.persistence.*;
import java.util.ArrayList;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder(toBuilder = true)
public class ChatRoom {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String roomName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ChatRoomMode mode = ChatRoomMode.random;   // ✅ 기본값 유지

  // ✅ 방장(생성자)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id")
  private User creator;

  // ✅ 생성 시각
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  // ✅ 멤버 관계
  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ChatRoomMember> members = new ArrayList<>();

  @PrePersist
  protected void onCreate() {
    if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    if (this.mode == null) this.mode = ChatRoomMode.random;
  }

  // 편의 메서드
  public void addMember(ChatRoomMember member) {
    this.members.add(member);
    member.setChatRoom(this);
  }
}