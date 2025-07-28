package com.jjpapa.vibetalk.login.service;

import com.jjpapa.vibetalk.login.abstraction.UserRepository;
import com.jjpapa.vibetalk.login.domain.dto.PasswordResetRequest;
import com.jjpapa.vibetalk.login.domain.dto.PasswordResetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public PasswordResetResponse resetPassword(PasswordResetRequest request) {
    var user = userRepository.findByEmail(request.getEmail())
        .orElse(null);

    if (user == null) {
      return new PasswordResetResponse(false, "등록되지 않은 이메일입니다.");
    }

    // 임시 비밀번호 생성
    String tempPassword = UUID.randomUUID().toString().substring(0, 8);
    user.setPassword(passwordEncoder.encode(tempPassword));
    userRepository.save(user);

    // TODO: 이메일로 tempPassword 발송 (MailSender 등 사용)

    return new PasswordResetResponse(true, "임시 비밀번호가 발급되었습니다: " + tempPassword);
  }
}
