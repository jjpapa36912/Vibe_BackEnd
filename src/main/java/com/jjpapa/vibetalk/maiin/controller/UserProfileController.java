package com.jjpapa.vibetalk.maiin.controller;

import com.jjpapa.vibetalk.login.abstraction.UserRepository;
import com.jjpapa.vibetalk.login.domain.dto.JwtUtil;
import com.jjpapa.vibetalk.login.domain.dto.UserProfileResponse;
import com.jjpapa.vibetalk.login.domain.entity.User;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserProfileController {

  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;

  @PostMapping(value = "/me/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserProfileResponse> updateProfile(
      @RequestHeader("Authorization") String token,
      @RequestParam(required = false) String statusMessage,
      @RequestParam(required = false) MultipartFile profileImage) throws IOException {

    String phoneNumber = jwtUtil.extractEmail(token);
    User user = userRepository.findByPhoneNumber(phoneNumber)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (statusMessage != null) {
      user.setStatusMessage(statusMessage);
    }

    if (profileImage != null && !profileImage.isEmpty()) {
      String uploadDir = System.getProperty("user.dir") + "/uploads/profile-images/";
      File dir = new File(uploadDir);
      if (!dir.exists()) dir.mkdirs();

      String filename = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
      File file = new File(dir, filename);
      profileImage.transferTo(file);

      user.setProfileImageUrl("/uploads/profile-images/" + filename);
      log.info("✅ 이미지 저장 경로: {}", file.getAbsolutePath());
    }

    userRepository.save(user);
    return ResponseEntity.ok(new UserProfileResponse(user));
  }
}

