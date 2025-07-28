package com.jjpapa.vibetalk.login.controller;

import com.jjpapa.vibetalk.login.domain.dto.PasswordResetRequest;
import com.jjpapa.vibetalk.login.domain.dto.PasswordResetResponse;
import com.jjpapa.vibetalk.login.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordController {
  private final PasswordService passwordService;

  @PostMapping("/reset-password")
  public ResponseEntity<PasswordResetResponse> resetPassword(@RequestBody PasswordResetRequest request) {
    return ResponseEntity.ok(passwordService.resetPassword(request));
  }
}
