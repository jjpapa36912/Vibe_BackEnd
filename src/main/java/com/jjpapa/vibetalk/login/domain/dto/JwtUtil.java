package com.jjpapa.vibetalk.login.domain.dto;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class JwtUtil {
  @Value("${jwt.secret}")
  private String secret;

  public String generateToken(String email) {
    return Jwts.builder()
        .setSubject(email)   // ✅ phoneNumber → email로 변경
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 86400000))
        .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
        .compact();
  }
  // ✅ JWT 검증 로직 추가
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
          .build()
          .parseClaimsJws(token.replace("Bearer ", ""));
      return true;
    } catch (Exception e) {
      return false;
    }
  }
  public String extractEmail(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
        .build()
        .parseClaimsJws(token.replace("Bearer ", ""))
        .getBody()
        .getSubject();
  }
}
