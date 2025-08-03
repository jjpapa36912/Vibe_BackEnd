package com.jjpapa.vibetalk.chat.config;

import com.jjpapa.vibetalk.chat.domain.dto.JwtChannelInterceptor;
import com.jjpapa.vibetalk.login.utils.JwtAuthenticationFilter;
import com.jjpapa.vibetalk.login.utils.JwtHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  private final JwtChannelInterceptor jwtChannelInterceptor;


  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    System.out.println("🚀 [Server] STOMP 엔드포인트 등록 시작 (/ws)");
    registry.addEndpoint("/ws")
        .addInterceptors(new JwtHandshakeInterceptor()) // ✅ Handshake에서 JWT 추출
        .setAllowedOriginPatterns("*"); // ✅ SockJS 제거
    System.out.println("✅ [Server] STOMP 엔드포인트 등록 완료");
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(jwtChannelInterceptor);
  }
}
