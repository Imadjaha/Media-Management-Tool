package com.example.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.AuthResponseDTO;
import com.example.backend.dto.LoginDTO;
import com.example.backend.dto.RegisterDTO;
import com.example.backend.exception.LoginException;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JWTGenerator;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  private final JWTGenerator jwtGenerator;

  public AuthController(
    AuthenticationManager authenticationManager,
    UserRepository userRepository,
    PasswordEncoder passwordEncoder,
    JWTGenerator jwtGenerator
  ) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtGenerator = jwtGenerator;
  }

  @PostMapping("register")
  public ResponseEntity<String> register(@RequestBody RegisterDTO registerDto) {
    if (userRepository.existsByUsername(registerDto.getUsername())) {
      return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
    }

    if (userRepository.existsByEmail(registerDto.getEmail())) {
      return ResponseEntity.badRequest().body("Email is taken!");
    }

    if (
      registerDto.getUsername() == null ||
      registerDto.getEmail() == null ||
      registerDto.getPassword() == null
    ) {
      return new ResponseEntity<>(
        "Please provide username, email and password",
        HttpStatus.BAD_REQUEST
      );
    }

    UserEntity user = new UserEntity();
    user.setUsername(registerDto.getUsername());
    user.setEmail(registerDto.getEmail());
    user.setPassword(passwordEncoder.encode((registerDto.getPassword())));

    userRepository.save(user);

    return new ResponseEntity<>(
      "User registered successfully",
      HttpStatus.CREATED
    );
  }

  @PostMapping("login")
  public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDto) {
    try {
      Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
          loginDto.getUsername(),
          loginDto.getPassword()
        )
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);

      String token = jwtGenerator.generateToken(authentication);

      return new ResponseEntity<>(
        new AuthResponseDTO(token, token),
        HttpStatus.OK
      );
    } catch (Exception ex) {
      throw new LoginException("Invalid username or password");
    }
  }
}
