package com.example.backend.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {

  private String accessToken;
  private String tokenType = "Bearer"; // Default value

  public AuthResponseDTO(String accessToken, String tokenType) {
    this.accessToken = accessToken;
    this.tokenType = tokenType;
  }

  // Constructor for accessToken with default tokenType
  public AuthResponseDTO(String accessToken) {
    this.accessToken = accessToken;
  }
}
