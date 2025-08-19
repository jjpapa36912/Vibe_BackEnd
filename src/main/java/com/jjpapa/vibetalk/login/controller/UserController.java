package com.jjpapa.vibetalk.login.controller;

import com.jjpapa.vibetalk.login.abstraction.UserRepository;
import com.jjpapa.vibetalk.login.domain.dto.JwtUtil;
import com.jjpapa.vibetalk.login.domain.dto.UserProfileResponse;
import com.jjpapa.vibetalk.login.domain.entity.User;
import com.jjpapa.vibetalk.maiin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;
  private final UserService userService;
  private final JwtUtil jwtUtil;



  @GetMapping("/me")
  public UserProfileResponse getProfile(@AuthenticationPrincipal User user) {
    return new UserProfileResponse(
        user.getId(),
        user.getName(),
        user.getStatusMessage(),
        user.getProfileImageUrl()
    );
  }

  // ========= (B) 계정 삭제 =========
  @DeleteMapping("/me")
  public ResponseEntity<Void> deleteMe(@RequestHeader("Authorization") String token) {
    String raw = token == null ? "" : token;
    String jwt = raw.replace("Bearer ", "").trim();

    String email = jwtUtil.extractEmail(jwt);
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    userService.deleteAccount(user.getId()); // 👈 연관 데이터 포함 정리

    return ResponseEntity.noContent().build(); // 204
  }

}