package com.jjpapa.vibetalk.login.domain.dto;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetResponse {
  private boolean success;
  private String message;
}
