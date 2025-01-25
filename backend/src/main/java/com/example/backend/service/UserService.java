package com.example.backend.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.backend.model.UserEntity;
import com.example.backend.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<UserEntity> getUsersIfFiveOrLess() {
    long userCount = userRepository.count();
    if (userCount <= 5) {
      return userRepository.findAll();
    } else {
      return Collections.emptyList();
    }
  }

  public Optional<UserEntity> getUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public UserEntity updateUser(Long userId, UserEntity userDetails) {
    UserEntity user = userRepository
      .findById(userId)
      .orElseThrow(() -> new RuntimeException("User not found"));
    user.setUsername(userDetails.getUsername());
    user.setPassword(userDetails.getPassword());
    user.setEmail(userDetails.getEmail());
    return userRepository.save(user);
  }

  public void deleteUser(Long userId) {
    userRepository.deleteById(userId);
  }
}
