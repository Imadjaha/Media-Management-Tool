package com.example.backend.security;

import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTGenerator {

  public String generateToken(Authentication authentication) {
    String username = authentication.getName();
    Date currentDate = new Date();
    Date expiryDate = new Date(
      currentDate.getTime() + SecurityConstants.JWT_EXPIRATION
    );

    String token = Jwts
      .builder()
      .setSubject(username)
      .setIssuedAt(currentDate)
      .setExpiration(expiryDate)
      .signWith(SecurityConstants.JWT_SECRET_KEY, SignatureAlgorithm.HS256)
      .compact();

    return token;
  }

  public String getUserNameFromJWT(String token) {
    Claims claims = Jwts
      .parserBuilder()
      .setSigningKey(SecurityConstants.JWT_SECRET_KEY)
      .build()
      .parseClaimsJws(token)
      .getBody();
    return claims.getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jwts
        .parserBuilder()
        .setSigningKey(SecurityConstants.JWT_SECRET_KEY)
        .build()
        .parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      throw new IllegalArgumentException("JWT token is expired or invalid");
    }
  }
}
