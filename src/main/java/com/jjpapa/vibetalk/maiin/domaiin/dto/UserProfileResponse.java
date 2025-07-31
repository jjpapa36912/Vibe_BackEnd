package com.jjpapa.vibetalk.maiin.domaiin.dto;

import com.jjpapa.vibetalk.login.domain.entity.User;

public class UserProfileResponse {
  private String name;
  private String statusMessage;
  private String profileImage;

  public UserProfileResponse(User user) {
    this.name = user.getName();
    this.statusMessage = user.getStatusMessage();
    this.profileImage = user.getProfileImageUrl();
  }
}
