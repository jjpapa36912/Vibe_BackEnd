package com.jjpapa.vibetalk.login.domain.dto;

import com.jjpapa.vibetalk.login.domain.entity.User;
import lombok.Getter;

@Getter
public class UserProfileResponse {
  private String name;
  private String statusMessage;
  private String profileImageUrl;  // ✅ 필드명 일치

  public UserProfileResponse(User user) {
    this.name = user.getName();
    this.statusMessage = user.getStatusMessage();
    this.profileImageUrl = user.getProfileImageUrl();
  }

  // ✅ Getter
}

