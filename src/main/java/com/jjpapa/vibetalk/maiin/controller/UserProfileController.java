package com.jjpapa.vibetalk.maiin.controller;

import com.jjpapa.vibetalk.login.abstraction.UserRepository;
import com.jjpapa.vibetalk.login.domain.dto.JwtUtil;
import com.jjpapa.vibetalk.login.domain.dto.UserProfileResponse;
import com.jjpapa.vibetalk.login.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserProfileController {

  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;

  // application.yml 또는 properties에서 경로 주입 (없으면 기본값 = 현재 실행 디렉토리/uploads)
  @Value("${app.upload-dir:#{systemProperties['user.dir'] + '/uploads'}}")
  private String uploadRoot;

  /**
   * 내 프로필 조회 (FriendList 진입 시 사용)
//   */
//  @GetMapping("/me")
//  public ResponseEntity<UserProfileResponse> me(@RequestHeader("Authorization") String token) {
//    String raw = token == null ? "" : token;
//    String jwt = raw.replace("Bearer ", "").trim();
//
//    String email = jwtUtil.extractEmail(jwt);
//    User user = userRepository.findByEmail(email)
//        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//    return ResponseEntity.ok(new UserProfileResponse(user));
//  }

  /**
   * 프로필 수정 (상태메시지, 프로필 이미지)
   */
  @PostMapping(value = "/me/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserProfileResponse> updateProfile(
      @RequestHeader("Authorization") String token,
      @RequestParam(required = false) String statusMessage,
      @RequestParam(required = false) MultipartFile profileImage
  ) throws IOException {

    String raw = token == null ? "" : token;
    String jwt = raw.replace("Bearer ", "").trim();

    String email = jwtUtil.extractEmail(jwt);
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    // 상태메시지 저장/삭제 처리
    if (statusMessage != null) {
      user.setStatusMessage(statusMessage.isBlank() ? null : statusMessage);
    }

    // 프로필 이미지 저장 처리
    if (profileImage != null && !profileImage.isEmpty()) {
      // profile-images 디렉토리 경로 생성
      String uploadDir = Paths.get(uploadRoot, "profile-images").toString();
      File dir = new File(uploadDir);

      if (!dir.exists()) {
        boolean created = dir.mkdirs();
        if (!created) {
          throw new IOException("❌ Could not create upload dir: " + uploadDir);
        }
        log.info("📁 Created directory: {}", dir.getAbsolutePath());
      }

      // 파일명 랜덤화
      String filename = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
      File file = new File(dir, filename);

      // 파일 저장
      profileImage.transferTo(file);
      log.info("✅ 이미지 저장 완료: {}", file.getAbsolutePath());

      // DB에는 웹에서 접근 가능한 public 경로 저장
      String publicPath = "/uploads/profile-images/" + filename;
      user.setProfileImageUrl(publicPath);
    }

    userRepository.save(user);
    log.info("💾 프로필 정보 업데이트 완료 - User ID: {}", user.getId());

    return ResponseEntity.ok(new UserProfileResponse(user));
  }
}
