package com.example.backend.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.model.PersonEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.PersonRepository;
import com.example.backend.service.PersonService;
import com.example.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PersonRepository personRepository;

  @MockBean
  private PersonService personService;

  @MockBean
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  public void setUp() {
    personRepository.deleteAll();
  }

  @Test
  @WithMockUser(username = "testUser") // simulate an authenticated user
  public void testCreateNewPerson() throws Exception {
    PersonEntity person = new PersonEntity(
      null,
      null,
      "testFirstName",
      "testLastName",
      "testAddress",
      "testEmail@test.com",
      "123456",
      LocalDateTime.now(),
      null
    );

    UserEntity user = new UserEntity();
    user.setUserId(1L);
    user.setUsername("testUser");

    when(userService.getUserByUsername("testUser"))
      .thenReturn(Optional.of(user));
    when(personService.createPerson(any(PersonEntity.class), any()))
      .thenReturn(person);

    mockMvc
      .perform(
        post("/api/persons")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(person))
      )
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "testUser")
  public void testGetPersonById() throws Exception {
    UserEntity user = new UserEntity();
    user.setUserId(1L);
    user.setUsername("testUser");
    PersonEntity person = new PersonEntity(
      1L,
      user,
      "testFirstName",
      "testLastName",
      "testAddress",
      "testEmail@test.com",
      "123456",
      LocalDateTime.now(),
      null
    );

    when(personService.getPersonById(1L)).thenReturn(Optional.of(person));

    mockMvc
      .perform(get("/api/persons/1").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.firstName").value("testFirstName"));
  }

  @Test
  @WithMockUser(username = "testUser")
  public void testGetPersonByIdNotFound() throws Exception {
    when(personService.getPersonById(1L)).thenReturn(Optional.empty());

    mockMvc
      .perform(get("/api/persons/1").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "testUser")
  public void testGetPersonsByUserId() throws Exception {
    UserEntity user = new UserEntity();
    user.setUserId(1L);
    user.setUsername("testUser");
    PersonEntity person = new PersonEntity(
      1L,
      user,
      "testFirstName",
      "testLastName",
      "testAddress",
      "testEmail@test.com",
      "123456",
      LocalDateTime.now(),
      null
    );

    when(personService.getPersonsByUserId(1L))
      .thenReturn(java.util.Arrays.asList(person));

    mockMvc
      .perform(
        get("/api/persons/user/1").contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].firstName").value("testFirstName"));
  }

  @Test
  @WithMockUser(username = "testUser")
  public void testDeletePersonById() throws Exception {
    UserEntity user = new UserEntity();
    user.setUserId(1L);
    user.setUsername("testUser");
    PersonEntity person = new PersonEntity(
      1L,
      user,
      "testFirstName",
      "testLastName",
      "testAddress",
      "testEmail@test.com",
      "123456",
      LocalDateTime.now(),
      null
    );

    when(personService.getPersonById(1L)).thenReturn(Optional.of(person));

    mockMvc
      .perform(delete("/api/persons/1").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(username = "testUser")
  public void testDeleteByUnexistingPersonId() throws Exception {
    when(personService.getPersonById(1L)).thenReturn(Optional.empty());

    mockMvc
      .perform(delete("/api/persons/1").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "testUser")
  public void testUpdatePersonById() throws Exception {
    UserEntity user = new UserEntity();

    when(userService.getUserByUsername("testUser"))
      .thenReturn(Optional.of(user));

    PersonEntity person = new PersonEntity(
      1L,
      user,
      "testFirstName",
      "testLastName",
      "testAddress",
      "testEmail@test.com",
      "123456",
      LocalDateTime.now(),
      null
    );

    PersonEntity updatedPerson = new PersonEntity(
      1L,
      user,
      "updatedFirstName",
      "updatedLastName",
      "updatedAddress",
      "updatedEmail@test.com",
      "987654321",
      LocalDateTime.now(),
      null
    );
    when(personRepository.findById(1L)).thenReturn(Optional.of(person));
    when(
      personService.updatePerson(
        eq(1L),
        any(PersonEntity.class),
        any(Authentication.class)
      )
    )
      .thenReturn(updatedPerson);

    mockMvc
      .perform(
        put("/api/persons/1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(updatedPerson))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.firstName").value("updatedFirstName"))
      .andExpect(jsonPath("$.lastName").value("updatedLastName"))
      .andExpect(jsonPath("$.address").value("updatedAddress"))
      .andExpect(jsonPath("$.email").value("updatedEmail@test.com"))
      .andExpect(jsonPath("$.phone").value("987654321"));

    verify(userService, times(1)).getUserByUsername("testUser");
    verify(personService, times(1))
      .updatePerson(eq(1L), any(PersonEntity.class), any(Authentication.class));
  }
}
