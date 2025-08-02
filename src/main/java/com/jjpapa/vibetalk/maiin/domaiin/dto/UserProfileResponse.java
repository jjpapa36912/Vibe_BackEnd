package com.jjpapa.vibetalk.maiin.domaiin.dto;

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
  public UserProfileResponse(User user) {
    this.id = user.getId(); // ✅ ID 추가
    this.name = user.getName();
    this.statusMessage = user.getStatusMessage();
    this.profileImageUrl = user.getProfileImageUrl();
  }
}
