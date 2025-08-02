package com.jjpapa.vibetalk.chat.controller;

import com.jjpapa.vibetalk.chat.domain.dto.ChatRoomListResponse;
import com.jjpapa.vibetalk.chat.domain.dto.ChatRoomResponse;
import com.jjpapa.vibetalk.chat.domain.entity.ChatMessage;
import com.jjpapa.vibetalk.chat.domain.entity.ChatRoom;
import com.jjpapa.vibetalk.chat.service.ChatService;
import com.jjpapa.vibetalk.login.abstraction.UserRepository;
import com.jjpapa.vibetalk.login.domain.dto.JwtUtil;
import com.jjpapa.vibetalk.login.domain.dto.UserProfileResponse;
import com.jjpapa.vibetalk.login.domain.entity.User;
import com.jjpapa.vibetalk.login.service.AuthService;
import java.nio.file.attribute.UserPrincipal;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

  private final ChatService chatService;
  private final AuthService authService;
  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;

  private final SimpMessagingTemplate messagingTemplate;


  @GetMapping("/api/chat/rooms")
  public ResponseEntity<List<ChatRoomListResponse>> getUserChatRooms(
      @RequestHeader("Authorization") String token) {

    String email = jwtUtil.extractEmail(token); // ✅ 같은 secret 사용
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found"));

    List<ChatRoom> rooms = chatService.getUserChatRooms(user.getId());
    List<ChatRoomListResponse> response = rooms.stream()
        .map(ChatRoomListResponse::from)
        .toList();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/chat/rooms/{roomId}/members")
  public ResponseEntity<List<UserProfileResponse>> getChatRoomMembers(
      @PathVariable Long roomId) {
    List<User> members = chatService.getChatRoomMembers(roomId);
    List<UserProfileResponse> response = members.stream()
        .map(UserProfileResponse::new)
        .toList();
    return ResponseEntity.ok(response);
  }


  @MessageMapping("/chat.sendMessage/{roomId}")
  public void sendMessage(@DestinationVariable Long roomId, ChatMessage message) {
    ChatMessage saved = chatService.saveMessage(roomId, message);

    // 방에 브로드캐스트
    messagingTemplate.convertAndSend("/topic/room." + roomId, saved);

    // 각 사용자에게 총 안 읽은 메시지 갱신 알림
    List<Long> participants = chatService.getRoomParticipants(roomId);
    for (Long userId : participants) {
      int totalUnread = chatService.getTotalUnreadMessages(userId);
      messagingTemplate.convertAndSend("/topic/unread/total/" + userId, totalUnread);
    }
  }

  @PostMapping("/api/chat/rooms/{roomId}/read")
  public void markAsRead(@PathVariable Long roomId, @RequestParam Long userId) {
    chatService.markRoomAsRead(roomId, userId);

    int totalUnread = chatService.getTotalUnreadMessages(userId);
    messagingTemplate.convertAndSend("/topic/unread/total/" + userId, totalUnread);
  }

  @GetMapping("/api/chat/rooms/unread/total")
  public int getTotalUnread(@RequestParam Long userId) {
    return chatService.getTotalUnreadMessages(userId);
  }

  @PostMapping("/api/chat/rooms")
  public ResponseEntity<ChatRoomResponse> createRoom(@RequestBody CreateRoomRequest request) {

    ChatRoom room = chatService.createRoom(request.getUserIds(), request.getCreatorId(), request.getRoomName());

    ChatRoomResponse response = new ChatRoomResponse(
        room.getId(),
        room.getRoomName()
    );

    return ResponseEntity.ok(response);
  }




  @Data
  static class CreateRoomRequest {
    private List<Long> userIds;
    private Long creatorId;
    private String roomName;
  }

  @Data
  static class ChatMessageDto {
    private Long senderId;
    private String message;
  }
}
