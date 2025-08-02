package com.jjpapa.vibetalk.login.controller;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketAuthController {

  @MessageMapping("/connect")
  public void handleConnectMessage(@Header("Authorization") String token) {
    System.out.println("🔑 CONNECT 수신 Authorization: " + token);
    // JWT 검증 로직 추가 가능
  }
}
