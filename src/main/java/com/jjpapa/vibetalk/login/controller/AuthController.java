package com.jjpapa.vibetalk.login.controller;

import com.jjpapa.vibetalk.login.domain.dto.LoginRequest;
import com.jjpapa.vibetalk.login.domain.dto.LoginResponse;
import com.jjpapa.vibetalk.login.domain.dto.SignupRequest;
import com.jjpapa.vibetalk.login.domain.dto.SignupResponse;
import com.jjpapa.vibetalk.login.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    LoginResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/register")
  public ResponseEntity<SignupResponse> register(@RequestBody SignupRequest request) {
    SignupResponse response = authService.register(request);
    return ResponseEntity.ok(response);
  }

}
