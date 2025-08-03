package com.jjpapa.vibetalk.chat.controller;

import com.jjpapa.vibetalk.chat.domain.dto.ChatMessageDto;
import com.jjpapa.vibetalk.chat.domain.dto.ChatMessageResponse;
import com.jjpapa.vibetalk.chat.domain.dto.ChatRoomListResponse;
import com.jjpapa.vibetalk.chat.domain.dto.ChatRoomResponse;
import com.jjpapa.vibetalk.chat.domain.dto.CreateChatRoomRequest;
import com.jjpapa.vibetalk.chat.domain.dto.StompPrincipal;
import com.jjpapa.vibetalk.chat.domain.entity.ChatMessage;
import com.jjpapa.vibetalk.chat.domain.entity.ChatRoom;
import com.jjpapa.vibetalk.chat.service.ChatService;
import com.jjpapa.vibetalk.login.abstraction.UserRepository;
import com.jjpapa.vibetalk.login.domain.dto.JwtUtil;
import com.jjpapa.vibetalk.login.domain.dto.UserProfileResponse;
import com.jjpapa.vibetalk.login.domain.entity.User;
import com.jjpapa.vibetalk.login.service.AuthService;
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
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
  public ResponseEntity<List<ChatRoomResponse>> getMyChatRooms(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    List<ChatRoomResponse> rooms = chatService.getChatRoomsForUser(user);
    return ResponseEntity.ok(rooms);
  }
  @GetMapping("/chatroom/{roomId}/messages")
  public ResponseEntity<List<ChatMessageResponse>> getMessages(
      @PathVariable Long roomId,
      @AuthenticationPrincipal StompPrincipal principal) {

    List<ChatMessageResponse> messages = chatService.getChatHistory(roomId);
    return ResponseEntity.ok(messages);
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
  public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto dto) {
    ChatMessage saved = chatService.saveMessage(roomId, dto);

    messagingTemplate.convertAndSend("/topic/room." + roomId, saved);

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
