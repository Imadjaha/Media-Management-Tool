package com.example.backend.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthEntryPoint authEntryPoint;

  private final CustomUserDetailsService userDetailsService;

  private final JWTGenerator jwtGenerator;

  public SecurityConfig(
    CustomUserDetailsService userDetailsService,
    JwtAuthEntryPoint authEntryPoint,
    JWTGenerator jwtGenerator
  ) {
    this.userDetailsService = userDetailsService;
    this.authEntryPoint = authEntryPoint;
    this.jwtGenerator = jwtGenerator;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))
      .exceptionHandling(exceptionHandling ->
        exceptionHandling.authenticationEntryPoint(authEntryPoint)
      )
      .sessionManagement(sessionManagement ->
        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .authorizeHttpRequests(authorize ->
        authorize
          // Allow public access to Swagger UI and OpenAPI documentation
          .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
          .permitAll()
          .requestMatchers(
            "/api/auth/**",
            "/api/auth/login",
            "/api/auth/register"
          )
          .permitAll() // Public endpoints (login, register, etc.)
          .requestMatchers(HttpMethod.GET, "/api/users/returnUsers")
          .permitAll()
          .requestMatchers("/api/**")
          .authenticated() // Require authentication for other /api/** endpoints
          // Require authentication for any other request
          .anyRequest()
          .authenticated()
      ) // Secure all other endpoints
      .addFilterBefore(
        jwtAuthenticationFilter(),
        UsernamePasswordAuthenticationFilter.class
      );

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(
    AuthenticationConfiguration authenticationConfiguration
  ) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // 12 is a higher strength, adjust as needed
  }

  @Bean
  public JWTAuthenticationFilter jwtAuthenticationFilter() {
    return new JWTAuthenticationFilter(jwtGenerator, userDetailsService);
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Allow requests from specified frontend ports
    configuration.setAllowedOrigins(
      Arrays.asList("http://localhost:5173", "http://localhost:3000")
    );

    // Allow all HTTP methods
    configuration.setAllowedMethods(
      Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")
    );

    // Allow all headers during development
    configuration.setAllowedHeaders(Arrays.asList("*"));

    // Allow credentials (for cookies, session, or JWT)
    configuration.setAllowCredentials(true);

    // Apply the configuration globally
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
