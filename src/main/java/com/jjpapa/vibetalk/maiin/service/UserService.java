package com.jjpapa.vibetalk.maiin.service;


import com.jjpapa.vibetalk.chat.abstraction.ChatMessageRepository;
import com.jjpapa.vibetalk.chat.abstraction.ChatRoomMemberRepository;
import com.jjpapa.vibetalk.chat.abstraction.ChatRoomRepository;
import com.jjpapa.vibetalk.login.abstraction.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
  private final ChatMessageRepository chatMessageRepository;
  private final ChatRoomMemberRepository chatRoomMemberRepository;
  private final ChatRoomRepository chatRoomRepository;
//  private final FriendRepository friendRepository;
  private final UserRepository userRepository;

  @Transactional
  public void deleteAccount(Long userId) {
    // 1) 내가 보낸 메시지 삭제 (자식)
    try {
      chatMessageRepository.deleteBySenderId(userId);
    } catch (Exception e) {
      log.warn("deleteBySenderId 실패: {}", e.getMessage());
    }

    // 2) 내 채팅방 멤버십 삭제 (자식)
    try {
      chatRoomMemberRepository.deleteByUserId(userId);
    } catch (Exception e) {
      log.warn("deleteByUserId(멤버십) 실패: {}", e.getMessage());
    }

    // 3) 멤버 0명인 방 정리 (선택)
    try {
      // 3-1) 멤버 없는 방 id 조회
      var orphanIds = chatRoomRepository.findIdsWithoutMembers();
      if (!orphanIds.isEmpty()) {
        // 3-2) 그 방들의 메시지 먼저 삭제(자식)
        chatMessageRepository.deleteByRoomIds(orphanIds);
        // 3-3) 그 다음 방 삭제(부모)
        chatRoomRepository.deleteAllByIdInBatch(orphanIds);
      }
    } catch (Exception e) {
      log.warn("빈 채팅방 정리 실패: {}", e.getMessage());
    }

    // 4) 마지막으로 사용자 삭제(부모)
    userRepository.deleteById(userId);
    log.info("✅ Account deleted: userId={}", userId);
  }

}
