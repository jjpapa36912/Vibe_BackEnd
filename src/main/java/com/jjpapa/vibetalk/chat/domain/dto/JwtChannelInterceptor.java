package com.jjpapa.vibetalk.chat.domain.dto;

import com.jjpapa.vibetalk.login.domain.dto.JwtUtil;
import java.util.List;
import java.util.Map;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

  private final JwtUtil jwtTokenProvider;

  public JwtChannelInterceptor(JwtUtil jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    StompCommand command = accessor.getCommand();

    // ✅ 디버깅 로그
    System.out.println("🔍 [JwtInterceptor] 전체 헤더: " + accessor.toNativeHeaderMap());
    System.out.println("🔑 Authorization(Native): " + accessor.getFirstNativeHeader("Authorization"));
    System.out.println("🔑 Session Attribute(Authorization): " + accessor.getSessionAttributes().get("Authorization"));

    // ✅ 1. STOMP 헤더에서 토큰 우선 가져오기
    String token = accessor.getFirstNativeHeader("Authorization");

    // ✅ 2. 없으면 HTTP Handshake 세션에서 가져오기
    if (token == null) {
      token = (String) accessor.getSessionAttributes().get("Authorization");
    }

    // ✅ 3. CONNECT, SEND, SUBSCRIBE 요청에 대해 JWT 검증
    if (command == StompCommand.CONNECT ||
        command == StompCommand.SEND ||
        command == StompCommand.SUBSCRIBE) {

      if (token != null && token.startsWith("Bearer ")) {
        token = token.substring(7); // "Bearer " 제거
        if (!jwtTokenProvider.validateToken(token)) {
          throw new IllegalArgumentException("Invalid JWT Token");
        }
      } else {
        throw new IllegalArgumentException("Missing JWT Token");
      }
    }

    return message;
  }



  /**
   * 인증이 필요한 STOMP 명령 정의
   */
  private boolean requiresAuthentication(StompCommand command) {
    return command == StompCommand.CONNECT ||
        command == StompCommand.SEND ||
        command == StompCommand.SUBSCRIBE;
  }

}
