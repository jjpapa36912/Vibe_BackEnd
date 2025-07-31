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

  public String generateToken(String phoneNumber) {
    return Jwts.builder()
        .setSubject(phoneNumber)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1일
        .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
        .compact();
  }

  public String extractPhoneNumber(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(
            StandardCharsets.UTF_8)))
        .build()
        .parseClaimsJws(token.replace("Bearer ", ""))
        .getBody()
        .getSubject();
  }

}
