package com.jjpapa.vibetalk.login.service;

import com.jjpapa.vibetalk.login.abstraction.UserRepository;
import com.jjpapa.vibetalk.login.domain.dto.JwtUtil;
import com.jjpapa.vibetalk.login.domain.dto.LoginRequest;
import com.jjpapa.vibetalk.login.domain.dto.LoginResponse;
import com.jjpapa.vibetalk.login.domain.dto.SignupRequest;
import com.jjpapa.vibetalk.login.domain.dto.SignupResponse;
import com.jjpapa.vibetalk.login.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;  // ✅ 추가

  public LoginResponse login(LoginRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new RuntimeException("User not found"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new RuntimeException("Invalid password");
    }

    // ✅ JWT 발급
    String token = jwtUtil.generateToken(user.getPhoneNumber());

    return new LoginResponse(
        user.getId(),
        user.getEmail(),
        user.getName(),
        token
    );
  }

  public SignupResponse register(SignupRequest request) {
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new RuntimeException("이미 가입된 이메일입니다.");
    }
    if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
      throw new RuntimeException("이미 가입된 전화번호입니다.");
    }

    User user = User.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .name(request.getName())
        .phoneNumber(request.getPhoneNumber())
        .build();

    userRepository.save(user);
    return new SignupResponse(user.getId(), user.getEmail(), user.getName());
  }
}
