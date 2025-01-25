package com.example.backend.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.backend.model.UserEntity;
import com.example.backend.repository.UserRepository;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private UserEntity user;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    user = new UserEntity();
    user.setUserId(1L);
    user.setUsername("testUser");
    user.setPassword("testPassword");
    user.setEmail("testEmail");
  }

  @Test
  public Optional<UserEntity> testGetUserByUsername() {
    when(userRepository.findByUsername("testUser"))
      .thenReturn(Optional.of(user));
    Optional<UserEntity> foundUser = userService.getUserByUsername("testUser");
    assertEquals(user, foundUser.get());
    return foundUser;
  }

  @Test
  public void testUpdateUserSuccess() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.save(any(UserEntity.class))).thenReturn(user);

    UserEntity updatedUser = userService.updateUser(1L, user);

    assertNotNull(updatedUser);
    assertEquals("testUser", updatedUser.getUsername());
    verify(userRepository, times(1)).findById(1L);
    verify(userRepository, times(1)).save(user);
  }

  @Test
  public void testDeleteUser() {
    doNothing().when(userRepository).deleteById(1L);

    userService.deleteUser(1L);

    verify(userRepository, times(1)).deleteById(1L);
  }

  @Test
  public void testGetUsersIfFiveOrLess_success() {
    List<UserEntity> expectedUsers = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      UserEntity user = new UserEntity();
      user.setUserId((long) i + 1); // Set unique IDs
      expectedUsers.add(user);
    }

    when(userRepository.findAll()).thenReturn(expectedUsers);

    List<UserEntity> actualUsers = userService.getUsersIfFiveOrLess();

    assertEquals(expectedUsers, actualUsers);
  }

  @Test
  public void testGetUsersIfMoreThanFive() {
    List<UserEntity> expectedUsers = new ArrayList<>();
    for (int i = 0; i < 6; i++) {
      UserEntity user = new UserEntity();
      user.setUserId((long) i + 1);
      user.setUsername("user" + i);
      user.setEmail("email" + i);
      user.setPassword("password" + i);
      expectedUsers.add(user);
    }

    when(userRepository.count()).thenReturn(6L);
    when(userRepository.findAll()).thenReturn(expectedUsers);

    List<UserEntity> actualUsers = userService.getUsersIfFiveOrLess();

    assertEquals(Collections.emptyList(), actualUsers);
  }
}
