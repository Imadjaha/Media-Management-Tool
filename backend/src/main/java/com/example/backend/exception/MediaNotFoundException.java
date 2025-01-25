package com.example.backend.exception;

public class MediaNotFoundException extends RuntimeException {

  public MediaNotFoundException(String message) {
    super(message);
  }
}
