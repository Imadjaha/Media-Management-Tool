package com.example.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.UserEntity;
import com.example.backend.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/returnUsers")
  public ResponseEntity<List<UserEntity>> getUsersIfFiveOrLess() {
    List<UserEntity> users = userService.getUsersIfFiveOrLess();
    if (users.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{username}")
  public ResponseEntity<UserEntity> getUserByUsername(
    @PathVariable String username
  ) {
    return userService
      .getUserByUsername(username)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserEntity> updateUser(
    @PathVariable Long id,
    @RequestBody UserEntity user
  ) {
    return ResponseEntity.ok(userService.updateUser(id, user));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
