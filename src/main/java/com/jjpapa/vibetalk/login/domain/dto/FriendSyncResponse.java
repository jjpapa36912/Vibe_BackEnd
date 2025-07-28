package com.jjpapa.vibetalk.login.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendSyncResponse {
  private Long userId;
  private String phoneNumber;
  private String appName;       // 앱 이름
  private String contactName;   // 내 연락처에서 저장된 이름
  private String statusMessage;
  private String profileImage;   // 내 연락처에서 저장한 이름
}