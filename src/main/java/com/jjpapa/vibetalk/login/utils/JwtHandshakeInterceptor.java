package com.jjpapa.vibetalk.login.utils;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

  @Override
  public boolean beforeHandshake(ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Map<String, Object> attributes) {

    String token = null;

    if (request instanceof org.springframework.http.server.ServletServerHttpRequest servletRequest) {
      var httpRequest = servletRequest.getServletRequest();

      // 🌐 URL과 QueryString 로그
      String url = httpRequest.getRequestURL().toString();
      String query = httpRequest.getQueryString();
      System.out.println("🌐 [HandshakeInterceptor] 요청 URL: " + url + "?" + query);

      // 1️⃣ URL 파라미터에서 token 추출
      token = httpRequest.getParameter("token");
      System.out.println("🔑 [HandshakeInterceptor] URL 파라미터 token: " + token);

      // 2️⃣ 헤더에서 Authorization 추출
      String authHeader = httpRequest.getHeader("Authorization");
      System.out.println("📜 [HandshakeInterceptor] Authorization 헤더: " + authHeader);

      if (token == null && authHeader != null && authHeader.startsWith("Bearer ")) {
        token = authHeader.substring(7);
        System.out.println("✅ [HandshakeInterceptor] 헤더에서 토큰 추출됨");
      }
    }

    if (token != null) {
      attributes.put("Authorization", "Bearer " + token);
      System.out.println("✅ [HandshakeInterceptor] 세션에 토큰 저장됨 → " + token);
    } else {
      System.out.println("⚠️ [HandshakeInterceptor] 토큰 없음");
    }

    return true;
  }



  @Override
  public void afterHandshake(ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Exception exception) {
    // Handshake 후 처리 없음
  }
}
