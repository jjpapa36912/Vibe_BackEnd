package com.jjpapa.vibetalk.chat.controller;

import com.jjpapa.vibetalk.chat.abstraction.ChatMessageRepository;
import com.jjpapa.vibetalk.chat.domain.dto.ChatMessageResponse;
import com.jjpapa.vibetalk.chat.domain.dto.ChatRoomResponse;
import com.jjpapa.vibetalk.chat.domain.dto.CreateChatRoomRequest;
import com.jjpapa.vibetalk.chat.domain.dto.SendChatMessageRequest;
import com.jjpapa.vibetalk.chat.domain.dto.UpdateMessageRequest;
import com.jjpapa.vibetalk.chat.domain.entity.ChatMessage;
import com.jjpapa.vibetalk.chat.service.ChatService;
import com.jjpapa.vibetalk.login.abstraction.UserRepository;
import com.jjpapa.vibetalk.login.domain.dto.JwtUtil;
import com.jjpapa.vibetalk.login.domain.dto.UserProfileResponse;
import com.jjpapa.vibetalk.login.domain.entity.User;
import com.jjpapa.vibetalk.login.service.AuthService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

  private final ChatService chatService;
  private final AuthService authService;
  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;
  private final ChatMessageRepository chatMessageRepository;

  private final SimpMessagingTemplate simpMessagingTemplate;


  @GetMapping("/api/chat/rooms")
  public ResponseEntity<List<ChatRoomResponse>> getMyChatRooms(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    List<ChatRoomResponse> rooms = chatService.getChatRoomsForUser(user);
    return ResponseEntity.ok(rooms);
  }

  // 최신 메시지 50개 로딩
//  @GetMapping("/api/chat/chatroom/{roomId}/messages")
//  public ResponseEntity<List<ChatMessageResponse>> getRecentMessages(
//      @PathVariable Long roomId,
//      @RequestParam(defaultValue = "50") int limit) {
//    return ResponseEntity.ok(chatService.getRecentMessages(roomId, limit));
//  }
  // 기존: @GetMapping("/api/chat/chatroom/{roomId}/messages")
  @GetMapping("/api/chat/rooms/{roomId}/messages")
  public ResponseEntity<List<ChatMessageResponse>> getRecentMessages(
      @PathVariable Long roomId,
      @RequestParam(defaultValue = "50") int limit,
      Authentication authentication
  ) {
    User user = (User) authentication.getPrincipal();
    log.info("getRecentMessages roomId={}, userId={}", roomId, user.getId());

    // (옵션) 멤버십 검증을 여기서 하시는 경우 403 사유를 JSON으로 돌려주면 디버깅 쉬움
    // if (!chatService.isMember(roomId, user.getId())) {
    //   return ResponseEntity.status(HttpStatus.FORBIDDEN)
    //     .body(Collections.emptyList());
    // }

    return ResponseEntity.ok(chatService.getRecentMessages(roomId, limit));
  }

  // 기존: @GetMapping("/{roomId}/messages/older")  // 베이스 경로 없음
  @GetMapping("/api/chat/rooms/{roomId}/messages/old")
  public ResponseEntity<List<ChatMessageResponse>> getOlderMessages(
      @PathVariable Long roomId,
      @RequestParam("before") String before, // ISO-8601 문자열
      @RequestParam(defaultValue = "50") int limit,
      Authentication authentication
  ) {
    User user = (User) authentication.getPrincipal();
    log.info("getOlderMessages roomId={}, userId={}, before={}", roomId, user.getId(), before);

    LocalDateTime beforeTime = LocalDateTime.parse(before);
    return ResponseEntity.ok(chatService.getOldMessages(roomId, beforeTime, limit));
  }

  // 기존: @GetMapping("/chat/rooms/{roomId}/members")
  @GetMapping("/api/chat/rooms/{roomId}/members")
  public ResponseEntity<List<UserProfileResponse>> getChatRoomMembers(
      @PathVariable Long roomId,
      Authentication authentication
  ) {
    User user = (User) authentication.getPrincipal();
    log.info("getChatRoomMembers roomId={}, userId={}", roomId, user.getId());

    List<User> members = chatService.getChatRoomMembers(roomId);
    List<UserProfileResponse> response = members.stream()
        .map(UserProfileResponse::new)
        .toList();
    return ResponseEntity.ok(response);
  }


//  @MessageMapping("/chat.sendMessage/{roomId}")
//  public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto dto) {
//    log.info("📩 [sendMessage] 채팅 메시지 수신 - roomId: {}, dto: {}", roomId, dto);
//
//    try {
//      ChatMessage saved = chatService.saveMessage(roomId, dto);
//      log.info("✅ [sendMessage] 메시지 DB 저장 완료: {}", saved.getId());
//
//      // 엔티티 → DTO 변환
//      ChatMessageDto responseDto = ChatMessageDto.fromEntity(saved);
//      log.info("🔄 [sendMessage] 엔티티 → DTO 변환 완료");
//
//      messagingTemplate.convertAndSend("/topic/room." + roomId, responseDto);
//      log.info("📤 [sendMessage] WebSocket 전송 완료 → /topic/room.{}", roomId);
//
//      List<Long> participants = chatService.getRoomParticipants(roomId);
//      log.info("👥 [sendMessage] 채팅방 참가자 수: {}", participants.size());
//
//      for (Long userId : participants) {
//        int totalUnread = chatService.getTotalUnreadMessages(userId);
//        messagingTemplate.convertAndSend("/topic/unread/total/" + userId, totalUnread);
//        log.info("🔔 [sendMessage] 안 읽은 메시지 수 전송 - userId: {}, count: {}", userId, totalUnread);
//      }
//    } catch (Exception e) {
//      log.error("❌ [sendMessage] 에러 발생: ", e);
//    }
//  }
// ChatController.java (요지)
@MessageMapping("/chat.sendMessage/{roomId}")
public void sendMessage(@DestinationVariable Long roomId,
    SendChatMessageRequest req,
    Principal principal) {
  // roomId 신뢰할 수 있게 강제
  SendChatMessageRequest fixed = new SendChatMessageRequest(
      roomId,
      req.senderId(),
      req.content(),
      req.clientMessageId(),
      req.sentAt(),
      req.emotion(),
      req.fontName(),
      req.emoji()
  );

  ChatMessage saved = chatService.saveOrGetByClientId(fixed);

  Map<String, Object> payload = new HashMap<>();
  payload.put("id", saved.getClientMessageId());    // 🔑 iOS 병합 키
  payload.put("serverId", saved.getId());           // 참고용(필요 시)
  payload.put("chatRoomId", roomId);
  payload.put("senderId", saved.getSender().getId());
  payload.put("senderName", saved.getSender().getName());
  payload.put("content", saved.getContent());
  payload.put("emotion", saved.getEmotion());
  payload.put("fontName", saved.getFontName());
  payload.put("emoji", saved.getEmoji());
  payload.put("sentAt", saved.getSentAt().toString());

  simpMessagingTemplate.convertAndSend("/topic/room." + roomId, payload);
}


  // 기존: @PostMapping("/api/chat/rooms/{roomId}/read") + @RequestParam Long userId
  @PostMapping("/api/chat/rooms/{roomId}/read")
  public ResponseEntity<Void> markAsRead(@PathVariable Long roomId, Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    Long userId = user.getId();

    chatService.markRoomAsRead(roomId, userId);

    int totalUnread = chatService.getTotalUnreadMessages(userId);
    simpMessagingTemplate.convertAndSend("/topic/unread/total/" + userId, totalUnread);
    return ResponseEntity.ok().build();
  }

  @MessageMapping("/chat.updateMessage/{roomId}")
  public void updateMessage(@DestinationVariable Long roomId,
      UpdateMessageRequest req) {
    // clientMessageId + senderId + roomId 로 조회
    ChatMessage m = chatMessageRepository
        .findByClientMessageIdAndSenderIdAndChatRoomId(
            req.clientMessageId(), req.senderId(), roomId)
        .orElseThrow(() -> new IllegalArgumentException("message not found"));

    if (req.emotion() != null)  m.setEmotion(req.emotion());
    if (req.fontName() != null) m.setFontName(req.fontName());
    if (req.emoji() != null)    m.setEmoji(req.emoji());
    chatMessageRepository.save(m);

    Map<String, Object> payload = new HashMap<>();
    payload.put("id", m.getClientMessageId()); // 🔑 클라 병합키 유지
    payload.put("serverId", m.getId());
    payload.put("chatRoomId", roomId);
    payload.put("senderId", m.getSender().getId());
    payload.put("senderName", m.getSender().getName());
    payload.put("content", m.getContent());
    payload.put("emotion", m.getEmotion());
    payload.put("fontName", m.getFontName());
    payload.put("emoji", m.getEmoji());
    payload.put("sentAt", m.getSentAt().toString());

    simpMessagingTemplate.convertAndSend("/topic/room." + roomId, payload);
  }

  @GetMapping("/api/chat/rooms/unread/total")
  public int getTotalUnread(@RequestParam Long userId) {
    return chatService.getTotalUnreadMessages(userId);
  }

  @PostMapping("/api/chat/rooms")
  public ResponseEntity<ChatRoomResponse> createChatRoom(
      @RequestBody CreateChatRoomRequest request,
      Principal principal) {

    // principal을 User로 캐스팅
    User creator = (User) ((Authentication) principal).getPrincipal();

    // 초대할 멤버 조회
    List<User> members = userRepository.findAllById(request.getMemberIds());

    ChatRoomResponse response = chatService.createGroupChatRoom(
        creator,
        members,
        request.getRoomName()
    );

    return ResponseEntity.ok(response);
  }





  @Data
  static class CreateRoomRequest {
    private List<Long> userIds;
    private Long creatorId;
    private String roomName;
  }

//  @Data
//  public static class ChatMessageDto {
//    private Long senderId;
//    private String message;
//  }
}
