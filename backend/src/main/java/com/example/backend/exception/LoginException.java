package com.example.backend.exception;

public class LoginException extends RuntimeException {

  public LoginException(String message) {
    super(message);
  }
}
