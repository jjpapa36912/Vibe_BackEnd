package com.jjpapa.vibetalk.chat.controller;

import com.jjpapa.vibetalk.login.abstraction.UserRepository;
import com.jjpapa.vibetalk.login.domain.entity.User;
import jakarta.transaction.Transactional;
import java.nio.file.attribute.UserPrincipal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class DeviceTokenController {

  private final UserRepository userRepository;

  @PostMapping("/device-token")
  @Transactional
  public ResponseEntity<Void> updateDeviceToken(@RequestBody Map<String, String> body) {
    String token = body.get("deviceToken");

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Object principal = authentication.getPrincipal();

    Long userId;
    if (principal instanceof User user) {
      userId = user.getId();  // ✅ User 엔티티에서 직접 ID 가져오기
    } else {
      userId = Long.valueOf(authentication.getName());
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("사용자 없음"));

    // ✅ 다른 사용자에게 동일 토큰이 있으면 제거
    int cleared = userRepository.clearTokenForOtherUsers(token, userId);
    if (cleared > 0) {
      log.info("🧹 다른 사용자 {}명에게서 동일 토큰 제거 완료", cleared);
    }

    // ✅ 현재 사용자 토큰 업데이트
    user.setDeviceToken(token);
    userRepository.save(user);

    return ResponseEntity.ok().build();
  }

//  @PostMapping("/device-token")
//  public ResponseEntity<Void> updateDeviceToken(@RequestBody Map<String, String> body) {
//    String token = body.get("deviceToken");
//
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    Object principal = authentication.getPrincipal();
//
//    Long userId;
//    if (principal instanceof User user) {
//      userId = user.getId();  // ✅ User 엔티티에서 직접 ID 가져오기
//    } else {
//      userId = Long.valueOf(authentication.getName());
//    }
//
//    User user = userRepository.findById(userId)
//        .orElseThrow(() -> new RuntimeException("사용자 없음"));
//
//    user.setDeviceToken(token);
//    userRepository.save(user);
//
//    return ResponseEntity.ok().build();
//  }

}

