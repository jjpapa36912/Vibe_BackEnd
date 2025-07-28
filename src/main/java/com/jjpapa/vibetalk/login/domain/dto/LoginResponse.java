package com.jjpapa.vibetalk.login.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
  private Long userId;
  private String email;
  private String name;
  private String token; // JWT or Session
}