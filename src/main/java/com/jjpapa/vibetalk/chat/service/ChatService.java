package com.jjpapa.vibetalk.chat.service;

import com.jjpapa.vibetalk.chat.abstraction.ChatMessageRepository;
import com.jjpapa.vibetalk.chat.abstraction.ChatRoomMemberRepository;
import com.jjpapa.vibetalk.chat.abstraction.ChatRoomRepository;
import com.jjpapa.vibetalk.chat.abstraction.UnreadMessageRepository;
import com.jjpapa.vibetalk.chat.domain.dto.ChatMessageDto;
import com.jjpapa.vibetalk.chat.domain.dto.ChatMessageResponse;
import com.jjpapa.vibetalk.chat.domain.dto.ChatRoomResponse;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

//  private final ChatRoomRepository roomRepo;
  private final ChatRoomMemberRepository chatRoomMemberRepository;
  private final ChatMessageRepository messageRepo;
  private final UnreadMessageRepository unreadRepo;
  private final SimpMessagingTemplate messagingTemplate;
  private final UserRepository userRepository;
  private final ChatRoomRepository chatRoomRepository;

  private final PushNotificationService pushNotificationService;

  @Transactional
  public void sendMessage(ChatMessageDto dto) {
    ChatRoom room = chatRoomRepository.findById(dto.getChatRoomId())
        .orElseThrow(() -> new IllegalArgumentException("채팅방이 없습니다."));
    User sender = userRepository.findById(dto.getSenderId())
        .orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다."));

    ChatMessage message = ChatMessage.builder()
        .chatRoom(room)
        .sender(sender)
        .content(dto.getContent())
        .sentAt(LocalDateTime.now())
        .build();

    messageRepo.save(message);

    messagingTemplate.convertAndSend(
        "/topic/room." + room.getId(),
        ChatMessageResponse.from(message)
    );
  }
//  public List<ChatMessageResponse> getRecentMessages(Long roomId, int limit) {
//    Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "sentAt"));
//    return messageRepo.findRecentMessages(roomId, pageable)
//        .stream()
//        .map(ChatMessageResponse::from)
//        .toList();
//  }
public List<ChatMessageResponse> getRecentMessages(Long roomId, int limit) {
  log.info("📩 [getRecentMessages] roomId: {}, limit: {}", roomId, limit);

  List<ChatMessageResponse> messages =
      messageRepo.findRecentMessagesDto(roomId, PageRequest.of(0, limit));

  log.info("✅ [getRecentMessages] 가져온 메시지 개수: {}", messages.size());
  return messages;
}

//  public List<ChatMessageResponse> getOldMessages(Long roomId, LocalDateTime beforeTime, int limit) {
//    log.info("📩 [getOldMessages] roomId: {}, beforeTime: {}, limit: {}", roomId, beforeTime, limit);
//
//    List<ChatMessage> messages = messageRepo.findOldMessages(roomId, beforeTime, PageRequest.of(0, limit));
//    log.info("✅ [getOldMessages] 가져온 이전 메시지 개수: {}", messages.size());
//
//    return messages.stream()
//        .map(ChatMessageResponse::from)
//        .toList();
//  }
// ✅ 무한 스크롤 과거 메시지
public List<ChatMessageResponse> getOldMessages(Long roomId, LocalDateTime beforeTime, int limit) {
  log.info("📩 [getOldMessages] roomId: {}, beforeTime: {}, limit: {}", roomId, beforeTime, limit);
  return messageRepo.findOldMessagesDto(roomId, beforeTime, PageRequest.of(0, limit));
}
  @Transactional
  public ChatMessage saveMessage(Long roomId, ChatMessageDto dto) {
    ChatRoom room = chatRoomRepository.findById(roomId)
        .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
    User sender = userRepository.findById(dto.getSenderId())
        .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

    ChatMessage message = ChatMessage.builder()
        .chatRoom(room)
        .sender(sender)
        .content(dto.getContent())
        .sentAt(LocalDateTime.now())
        .build();

    ChatMessage saved = messageRepo.save(message);

    List<ChatRoomMember> participants = chatRoomMemberRepository.findByChatRoomId(roomId);

    for (ChatRoomMember member : participants) {
      if (!member.getUser().getId().equals(sender.getId())) {
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
  @Transactional
  public ChatRoomResponse createGroupChatRoom(User creator, List<User> members, String roomName) {
    if (members.size() > 7) {
      throw new IllegalArgumentException("채팅방 최대 인원은 8명입니다.");
    }

    // 중복 제거 + 생성자 포함
    Set<User> uniqueMembers = new HashSet<>(members);
    uniqueMembers.add(creator);

    List<Long> memberIds = uniqueMembers.stream()
        .map(User::getId)
        .toList();

    // 기존 방 조회
    List<Long> existingRooms = chatRoomRepository.findRoomWithExactMembers(memberIds, memberIds.size());
    if (!existingRooms.isEmpty()) {
      Long roomId = existingRooms.get(0);
      ChatRoom existingRoom = chatRoomRepository.findById(roomId)
          .orElseThrow(() -> new IllegalStateException("방이 존재하지 않습니다."));
      return ChatRoomResponse.from(existingRoom);
    }

    // 새로운 방 생성
    ChatRoom room = ChatRoom.builder()
        .roomName(roomName)
//        .createdBy(creator.getId())
        .build();
    chatRoomRepository.save(room);

    for (User user : uniqueMembers) {
      ChatRoomMember member = ChatRoomMember.builder()
          .chatRoom(room)
          .user(user)
          .joinedAt(LocalDateTime.now())
          .build();
      chatRoomMemberRepository.save(member);
    }

    return ChatRoomResponse.from(room);
  }



  public List<User> getChatRoomMembers(Long roomId) {
    return chatRoomMemberRepository.findByChatRoomId(roomId)
        .stream()
        .map(ChatRoomMember::getUser) // ✅ userId 대신 user 객체 반환
        .collect(Collectors.toList());
  }
  @Transactional
  public List<ChatRoomResponse> getChatRoomsForUser(User user) {
    List<ChatRoom> rooms = chatRoomRepository.findAllByMember(user.getId());

    return rooms.stream().map(room -> {
      List<User> members = chatRoomMemberRepository.findUsersByRoomId(room.getId());
      // 현재 로그인 유저 제외
      String displayName = members.stream()
          .filter(member -> !member.getId().equals(user.getId()))
          .map(User::getName)
          .collect(Collectors.joining(", "));

      // 그룹 이름이 비어 있으면 fallback
      if (displayName.isBlank()) {
        displayName = room.getRoomName();
      }

      return new ChatRoomResponse(room.getId(), displayName);
    }).collect(Collectors.toList());
  }


  public List<ChatRoom> getUserChatRooms(Long userId) {
    return chatRoomRepository.findByUserId(userId);
  }

  @Transactional
  public ChatMessageResponse sendMessage(ChatMessageDto messageDto, User sender) {
    ChatRoom room = chatRoomRepository.findById(messageDto.getChatRoomId())
        .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

    ChatMessage message = ChatMessage.builder()
        .chatRoom(room)
        .sender(sender)
        .content(messageDto.getContent())
        .sentAt(LocalDateTime.now())
        .build();
    messageRepo.save(message);

    // STOMP로 전송
    messagingTemplate.convertAndSend(
        "/topic/chatroom/" + room.getId(),
        ChatMessageResponse.from(message)
    );

    return ChatMessageResponse.from(message);
  }
  @Transactional
  public List<ChatMessageResponse> getChatHistory(Long roomId) {
    log.info("📥 [ChatService] getChatHistory 호출 - roomId: {}", roomId);

    List<ChatMessage> messages = messageRepo.findByChatRoomIdOrderBySentAtAsc(roomId);

    log.info("✅ [ChatService] 조회된 메시지 수: {}", messages.size());

    return messages.stream()
        .map(ChatMessageResponse::from)
        .toList();
  }


}
