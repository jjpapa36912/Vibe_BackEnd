package com.jjpapa.vibetalk.login.controller;

import com.jjpapa.vibetalk.login.abstraction.UserRepository;
import com.jjpapa.vibetalk.login.domain.dto.JwtUtil;
import com.jjpapa.vibetalk.login.domain.dto.UserProfileResponse;
import com.jjpapa.vibetalk.login.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;



  @GetMapping("/me")
  public ResponseEntity<UserProfileResponse> getMyProfile(
      @RequestHeader("Authorization") String token) {

    String phoneNumber = jwtUtil.extractPhoneNumber(token);
    User user = userRepository.findByPhoneNumber(phoneNumber)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return ResponseEntity.ok(new UserProfileResponse(user));
  }
}