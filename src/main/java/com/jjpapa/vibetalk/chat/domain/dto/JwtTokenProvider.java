//package com.jjpapa.vibetalk.chat.domain.dto;
//
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.stereotype.Component;
//
//import java.security.Key;
//
//@Component
//public class JwtTokenProvider {
//
//  private final Key key;
//
//  public JwtTokenProvider() {
//    String secret = "your_secret_key_for_jwt_your_secret_key_for_jwt"; // 256-bit
//    this.key = Keys.hmacShaKeyFor(secret.getBytes());
//  }
//
//  public String getUsernameFromToken(String token) {
//    return parseClaims(token).getSubject();  // subject에 email이 들어있음
//  }
//
//  public boolean validateToken(String token) {
//    try {
//      parseClaims(token);
//      return true;
//    } catch (Exception e) {
//      return false;
//    }
//  }
//
//  private Claims parseClaims(String token) {
//    return Jwts.parserBuilder()
//        .setSigningKey(key)
//        .build()
//        .parseClaimsJws(token)
//        .getBody();
//  }
//}
//
