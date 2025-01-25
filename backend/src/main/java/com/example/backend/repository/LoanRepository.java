package com.example.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.model.LoanEntity;
import com.example.backend.model.PersonEntity;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
  @SuppressWarnings("null")
  @Override
  List<LoanEntity> findAll();

  List<LoanEntity> findByPerson_User_UserIdAndReturnedAtIsNull(Long userId);

  @Query("SELECT l FROM LoanEntity l WHERE l.person IN :persons")
  List<LoanEntity> findByPersons(@Param("persons") List<PersonEntity> persons);

  List<LoanEntity> findByPerson_User_UserIdAndDueDateBeforeAndReturnedAtIsNull(
    Long userId,
    LocalDate currentDate
  );
    @Query("SELECT l FROM LoanEntity l WHERE l.returnedAt IS NULL AND l.dueDate <= :dueDate")
    List<LoanEntity> findAllDueToday(@Param("dueDate") LocalDate dueDate);

}
