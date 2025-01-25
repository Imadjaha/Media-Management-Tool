package com.example.backend.service;

import java.util.List;
import java.util.Optional;

import com.example.backend.model.*;
import com.example.backend.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.backend.repository.PersonRepository;

@Service
public class PersonService {

  private final PersonRepository personRepository;

  @Autowired
  private final UserService userService;
    @Autowired 
    private MediaRepository mediaRepository;

    public PersonService(
    PersonRepository personRepository,
    UserService userService
  ) {
    this.userService = userService;
    this.personRepository = personRepository;
  }

  public List<PersonEntity> getAllPersons() {
    return personRepository.findAll();
  }

  public Optional<PersonEntity> getPersonById(Long personId) {
    return personRepository.findById(personId);
  }

  public List<PersonEntity> getPersonsByUserId(Long userId) {
    return personRepository.findByUserUserId(userId);
  }

  public List<PersonEntity> getPersonsByUsername(String username) {
    Optional<UserEntity> userOptional = userService.getUserByUsername(username);
    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }
    UserEntity user = userOptional.get();
    return personRepository.findByUserUserId(user.getUserId());
  }

  public PersonEntity createPerson(
    PersonEntity person,
    Authentication authentication
  ) {
    String username = authentication.getName();
    Optional<UserEntity> userOptional = userService.getUserByUsername(username);
    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }
    UserEntity user = userOptional.get();
    person.setUser(user);
    return personRepository.save(person);
  }

  public PersonEntity updatePerson(
    Long personId,
    PersonEntity currentPerson,
    Authentication authentication
  ) {
    String username = authentication.getName();
    Optional<UserEntity> userOptional = userService.getUserByUsername(username);

    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }

    UserEntity user = userOptional.get();
    currentPerson.setUser(user);

    PersonEntity updatedPerson = personRepository
      .findById(personId)
      .orElseThrow(() -> new RuntimeException("Person not found"));

    updatedPerson.setFirstName(currentPerson.getFirstName());
    updatedPerson.setLastName(currentPerson.getLastName());
    updatedPerson.setAddress(currentPerson.getAddress());
    updatedPerson.setEmail(currentPerson.getEmail());
    updatedPerson.setPhone(currentPerson.getPhone());
    updatedPerson.setUser(currentPerson.getUser());
    return personRepository.save(updatedPerson);
  }

  public void deletePerson(Long id) {
    try {
      Optional<PersonEntity> person = personRepository.findById(id);

      if (person.isEmpty()) {
        throw new IllegalArgumentException(
          "Person with ID " + id + " does not exist."
        );
      }
    for(LoanEntity loan: person.get().getLoans()){
        MediaEntity media = loan.getMedia();
        media.setMediaState(MediaState.AVAILABLE);
        mediaRepository.save(media);
        System.out.printf("media id" + media.getMediaId() + "medianem" + media.getTitle() + "\n");
    }
      personRepository.deleteById(id);
    } catch (Exception e) {
      throw new RuntimeException(
        "An error occurred while deleting person with ID " + id,
        e
      );
    }
  }
}
