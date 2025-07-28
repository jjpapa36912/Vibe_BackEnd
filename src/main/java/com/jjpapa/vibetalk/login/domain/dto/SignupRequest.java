package com.jjpapa.vibetalk.login.domain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
  private String email;
  private String password;
  private String name;
  private String phoneNumber;
}