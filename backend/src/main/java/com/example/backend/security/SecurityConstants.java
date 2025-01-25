package com.example.backend.security;

import java.security.Key;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class SecurityConstants {

  public static final long JWT_EXPIRATION = 5184000000L; // 2 months in milliseconds
  // hs256 is a symmetric algorithm and it gives us a key to encrypt and decrypt
  public static final Key JWT_SECRET_KEY = Keys.secretKeyFor(
    SignatureAlgorithm.HS256
  ); // Securely generated key
}
