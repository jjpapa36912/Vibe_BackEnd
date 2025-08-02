package com.jjpapa.vibetalk.chat.service;

import com.jjpapa.vibetalk.chat.abstraction.ChatMessageRepository;
import com.jjpapa.vibetalk.chat.abstraction.ChatRoomMemberRepository;
import com.jjpapa.vibetalk.chat.abstraction.ChatRoomRepository;
import com.jjpapa.vibetalk.chat.abstraction.UnreadMessageRepository;
import com.jjpapa.vibetalk.chat.domain.entity.ChatMessage;
import com.jjpapa.vibetalk.chat.domain.entity.ChatRoom;
import com.jjpapa.vibetalk.chat.domain.entity.ChatRoomMember;
import com.jjpapa.vibetalk.chat.domain.entity.UnreadMessage;
import com.jjpapa.vibetalk.login.abstraction.UserRepository;
import com.jjpapa.vibetalk.login.domain.entity.User;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final ChatRoomRepository roomRepo;
  private final ChatRoomMemberRepository chatRoomMemberRepository;
  private final ChatMessageRepository messageRepo;
  private final UnreadMessageRepository unreadRepo;
  private final SimpMessagingTemplate messagingTemplate;
  private final UserRepository userRepository;
  private final ChatRoomRepository chatRoomRepository;

  private final PushNotificationService pushNotificationService;

  @Transactional
  public ChatMessage saveMessage(Long roomId, ChatMessage message) {
    message.setRoomId(roomId);
    ChatMessage saved = messageRepo.save(message);

    // 방 참여자 찾기
    List<ChatRoomMember> participants = chatRoomMemberRepository.findByChatRoomId(roomId);

    // UnreadMessage 생성
    for (ChatRoomMember member : participants) {
      if (!member.getUser().getId().equals(message.getSenderId())) {
        UnreadMessage unread = new UnreadMessage();
        unread.setUserId(member.getUser().getId());
        unread.setRoomId(roomId);
        unread.setMessageId(saved.getId());
        unreadRepo.save(unread);
      }
    }
    return saved;
  }

  public List<Long> getRoomParticipants(Long roomId) {
    return chatRoomMemberRepository.findByChatRoomId(roomId)
        .stream()
        .map(member -> member.getUser().getId())
        .toList();
  }

  public int getTotalUnreadMessages(Long userId) {
    return unreadRepo.countByUserId(userId);
  }

  @Transactional
  public void markRoomAsRead(Long roomId, Long userId) {
    unreadRepo.deleteByUserIdAndRoomId(userId, roomId);
  }

  @Transactional
  public ChatRoom createRoom(List<Long> userIds, Long creatorId, String roomName) {
    System.out.println("📌 [ChatRoomService] 채팅방 생성 요청: userIds=" + userIds + ", creatorId=" + creatorId);

    // ✅ 1. 채팅방 생성
    ChatRoom room = new ChatRoom();
    room.setRoomName(roomName);
    ChatRoom savedRoom = chatRoomRepository.save(room);
    System.out.println("✅ [ChatRoomService] 채팅방 생성 완료: roomId=" + savedRoom.getId());

    // ✅ 2. 멤버 추가 (생성자 포함)
    List<Long> allUserIds = new ArrayList<>(userIds);
    if (!allUserIds.contains(creatorId)) {
      allUserIds.add(creatorId);
    }

    for (Long userId : allUserIds) {
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new RuntimeException("User not found: " + userId));

      ChatRoomMember member = new ChatRoomMember();
      member.setChatRoom(savedRoom);
      member.setUser(user); // ✅ user 설정
      member.setJoinedAt(LocalDateTime.now());
      chatRoomMemberRepository.save(member);
    }
    System.out.println("✅ [ChatRoomService] 멤버 추가 완료: count=" + allUserIds.size());

    // ✅ 3. 초대받은 유저들에게 푸시 알림 발송
    List<User> invitedUsers = userRepository.findAllById(allUserIds);
    for (User user : invitedUsers) {
      if (user.getId().equals(creatorId)) continue; // 방 생성자는 알림 제외

      String deviceToken = user.getDeviceToken();
      if (deviceToken != null && !deviceToken.isEmpty()) {
        System.out.println("📤 [ChatRoomService] 푸시 발송 대상 → userId=" + user.getId());

        // 🔑 Map 타입을 맞추기 위해 roomId를 문자열로 변환
        Map<String, String> data = new HashMap<>();
        data.put("roomId", String.valueOf(savedRoom.getId()));

        pushNotificationService.sendPushNotification(
            deviceToken,
            "새 그룹 채팅방",
            user.getName() + "님이 초대되었습니다: " + roomName,
            data
        );

      } else {
        System.out.println("⚠️ [ChatRoomService] userId=" + user.getId() + " → 푸시 토큰 없음 (알림 생략)");
      }
    }

    return savedRoom;
  }

  public List<User> getChatRoomMembers(Long roomId) {
    return chatRoomMemberRepository.findByChatRoomId(roomId)
        .stream()
        .map(ChatRoomMember::getUser) // ✅ userId 대신 user 객체 반환
        .collect(Collectors.toList());
  }

  public List<ChatRoom> getUserChatRooms(Long userId) {
    return roomRepo.findByUserId(userId);
  }
}
