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

//  @Override
//  public void registerStompEndpoints(StompEndpointRegistry registry) {
//    registry.addEndpoint("/ws")
//        .setAllowedOriginPatterns("*")
//        .withSockJS();
//  }
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .addInterceptors(new JwtHandshakeInterceptor()) // ✅ 추가
        .setAllowedOriginPatterns("*")
        .withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(jwtChannelInterceptor);
  }
}
