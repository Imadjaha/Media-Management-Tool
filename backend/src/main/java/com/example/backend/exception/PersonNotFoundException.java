package com.example.backend.exception;

public class PersonNotFoundException extends RuntimeException {

  public PersonNotFoundException(String message) {
    super(message);
  }
}
