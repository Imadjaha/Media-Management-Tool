package com.example.backend.controller;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.model.UserEntity;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JWTGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.backend.dto.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JWTGenerator jwtGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void testRegisterSuccessfully() throws Exception {
        RegisterDTO registerDto = new RegisterDTO(
                "testUser",
                "testPassword",
                "test@email.com"
        );

        when(userRepository.existsByUsername("testUser")).thenReturn(false);

        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(passwordEncoder.encode("testPassword")).thenReturn("encodedPassword");

        mockMvc
                .perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerDto))
                )
                .andExpect(status().isCreated()) // status is 201
                .andExpect(content().string("User registered successfully"));

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    public void testRegisterUsernameTaken() throws Exception {
        RegisterDTO registerDto = new RegisterDTO(
                "testUser",
                "testPassword",
                "test@email.com"
        );

        when(userRepository.existsByUsername("testUser")).thenReturn(true);

        mockMvc
                .perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username is taken!"));

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void testRegisterEmailTaken() throws Exception {
        RegisterDTO registerDto = new RegisterDTO(
                "testUser",
                "testPassword",
                "test@email.com"
        );

        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(userRepository.existsByEmail("test@email.com")).thenReturn(true);

        mockMvc
                .perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email is taken!"));

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void testRegisterWithUncompleteData() throws Exception {
        RegisterDTO registerDto = new RegisterDTO("testUser", "testPassword", null);

        mockMvc
                .perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().string("Please provide username, email and password")
                );

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    public void testLoginSuccessfully() throws Exception {
        LoginDTO loginDto = new LoginDTO("testUser", "testPassword");

        when(authenticationManager.authenticate(any()))
                .thenReturn(mock(org.springframework.security.core.Authentication.class));
        when(jwtGenerator.generateToken(any())).thenReturn("mockedJWTToken");

        mockMvc
                .perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mockedJWTToken"));

        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtGenerator, times(1)).generateToken(any());
    }

    @Test
    public void testLoginWithInvalidCredentials() throws Exception {
        LoginDTO loginDto = new LoginDTO("testUser", "invalidPassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Authentication failed"));

        mockMvc
                .perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginDto))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(
                        jsonPath("$.accessToken").value("Invalid username or password")
                );

        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtGenerator, never()).generateToken(any());
    }
}
