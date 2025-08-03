package com.jjpapa.vibetalk.chat.domain.dto;

import com.jjpapa.vibetalk.login.domain.dto.JwtUtil;
import java.util.List;
import java.util.Map;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

  private final JwtUtil jwtTokenProvider;

  public JwtChannelInterceptor(JwtUtil jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }


  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    StompCommand command = accessor != null ? accessor.getCommand() : null;

    System.out.println("🔍 [JwtInterceptor] 전체 헤더: " + (accessor != null ? accessor.toNativeHeaderMap() : "null"));
    if (accessor != null) {
      System.out.println("🔑 Authorization(Native): " + accessor.getFirstNativeHeader("Authorization"));
      if (accessor.getSessionAttributes() != null) {
        System.out.println("🔑 Session Attribute(Authorization): " +
            accessor.getSessionAttributes().get("Authorization"));
      }
    }

    String token = null;

    // 1️⃣ Native Header에서
    if (accessor != null && accessor.getFirstNativeHeader("Authorization") != null) {
      token = accessor.getFirstNativeHeader("Authorization");
    }

    // 2️⃣ Handshake 세션에서
    if (token == null && accessor != null && accessor.getSessionAttributes() != null) {
      token = (String) accessor.getSessionAttributes().get("Authorization");
    }

    // 3️⃣ CONNECT일 경우: Handshake에서 세션으로 인증, 예외 발생하지 않도록 함
    if (command == StompCommand.CONNECT) {
      if (token == null) {
        System.out.println("⚠️ [JwtInterceptor] CONNECT 단계에서 토큰 없음 - Handshake 인증만 사용");
        return message;
      }
    }

    // SEND, SUBSCRIBE 등에서는 토큰 필수
    if (command == StompCommand.SEND || command == StompCommand.SUBSCRIBE) {
      if (token != null && token.startsWith("Bearer ")) {
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
          throw new IllegalArgumentException("Invalid JWT Token");
        }
      } else {
        throw new IllegalArgumentException("Missing JWT Token");
      }
    }

    return message;
  }
}
