package com.jjpapa.vibetalk.login.domain.dto;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {
  private String email;
}
