package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.model.PersonEntity;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
  List<PersonEntity> findByUserUserId(Long userId);

  @Query(
    "SELECT p FROM PersonEntity p WHERE p.firstName LIKE :firstName% AND (:lastName IS NULL OR p.lastName LIKE :lastName%)"
  )
  List<PersonEntity> findByFirstNameStartsWithAndLastNameStartsWith(
    @Param("firstName") String firstName,
    @Param("lastName") String lastName
  );
}
