package com.jjpapa.vibetalk.login.domain.dto;

import com.jjpapa.vibetalk.login.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
  private Long id;  // ✅ 추가
  private String name;
  private String statusMessage;
  private String profileImageUrl;
  // ✅ User → DTO 변환 생성자 추가
  public UserProfileResponse(User user) {
    this.id = user.getId();
    this.name = user.getName();
    this.statusMessage = user.getStatusMessage();
    this.profileImageUrl = user.getProfileImageUrl();
  }
}

