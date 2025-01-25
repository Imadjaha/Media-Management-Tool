package com.example.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.PersonEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.service.PersonService;
import com.example.backend.service.UserService;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

  private final PersonService personService;

  @Autowired
  private UserService userService;

  public PersonController(PersonService personService) {
    this.personService = personService;
  }

  @GetMapping
  public List<PersonEntity> getAllPersons() {
    return personService.getAllPersons();
  }

  @GetMapping("/{id}")
  public ResponseEntity<PersonEntity> getPersonById(@PathVariable Long id) {
    return personService
      .getPersonById(id)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/by-username")
  public List<PersonEntity> getPersonsByUsername(
    Authentication authentication
  ) {
    String username = authentication.getName();
    return personService.getPersonsByUsername(username);
  }

  @GetMapping("/user/{userId}")
  public List<PersonEntity> getPersonsByUserId(@PathVariable Long userId) {
    return personService.getPersonsByUserId(userId);
  }

  @PostMapping
  public PersonEntity createPerson(
    @RequestBody PersonEntity person,
    Authentication authentication
  ) {
    String username = authentication.getName();
    Optional<UserEntity> userOptional = userService.getUserByUsername(username);
    UserEntity user = userOptional.get();
    person.setUser(user);
    return personService.createPerson(person, authentication);
  }

  @PutMapping("/{personId}")
  public ResponseEntity<PersonEntity> updatePerson(
    @PathVariable Long personId,
    @RequestBody PersonEntity currentPerson,
    Authentication authentication
  ) {
    String username = authentication.getName();
    Optional<UserEntity> userOptional = userService.getUserByUsername(username);
    UserEntity user = userOptional.get();
    currentPerson.setUser(user);

    PersonEntity updatedPerson = personService.updatePerson(
      personId,
      currentPerson,
      authentication
    );

    return ResponseEntity.ok(updatedPerson);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePerson(
    @PathVariable Long id,
    Authentication authentication
  ) {
    String username = authentication.getName();
    Optional<PersonEntity> personOptional = personService.getPersonById(id);

    if (personOptional.isEmpty()) {
      System.out.println("Person not found with ID: " + id);
      return ResponseEntity.notFound().build();
    }

    PersonEntity person = personOptional.get();

    if (!person.getUser().getUsername().equals(username)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    personService.deletePerson(id);
    return ResponseEntity.noContent().build();
  }
}
