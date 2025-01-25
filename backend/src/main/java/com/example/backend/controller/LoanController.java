package com.example.backend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.exception.MediaNotFoundException;
import com.example.backend.exception.PersonNotFoundException;
import com.example.backend.model.LoanEntity;
import com.example.backend.service.LoanService;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

  @Autowired
  private final LoanService loanService;

  public LoanController(LoanService loanService) {
    this.loanService = loanService;
  }

  @GetMapping("/all")
  public ResponseEntity<List<LoanEntity>> getLoansByUser() {
    List<LoanEntity> loans = loanService.getLoansByUser();
    return new ResponseEntity<>(loans, HttpStatus.OK);
  }

  @GetMapping("/active")
  public ResponseEntity<List<LoanEntity>> getActiveLoansByUser() {
    List<LoanEntity> activeLoans = loanService.getActiveLoansByUser();
    return new ResponseEntity<>(activeLoans, HttpStatus.OK);
  }

  @GetMapping("/overdue")
  public ResponseEntity<List<LoanEntity>> getOverdueLoansByUser(
    @RequestParam(required = false) LocalDate currentDate
  ) {
    if (currentDate == null) {
      currentDate = LocalDate.now(); // Standardwert: heutiges Datum
    }
    List<LoanEntity> overdueLoans = loanService.getOverdueLoansByUser(
      currentDate
    );
    return new ResponseEntity<>(overdueLoans, HttpStatus.OK);
  }

  @PutMapping("/{loanId}/return")
  public ResponseEntity<Void> markAsReturned(
    @PathVariable Long loanId,
    @RequestBody Map<String, String> payload
  ) {
    String returnedAtString = payload.get("returnedAt");
    LocalDateTime returnedAt = LocalDateTime.parse(
      returnedAtString,
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    );
    loanService.markAsReturned(loanId, returnedAt);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping("/{mediaId}/{personId}")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<LoanEntity> createLoan(
    @PathVariable Long mediaId,
    @PathVariable Long personId,
    Authentication authentication,
    @RequestParam(required = false) LocalDate dueDate,
    @RequestParam(required = false) LocalDateTime borrowedAt
  ) {
    try {
      LoanEntity createdLoan = loanService.createLoan(
        mediaId,
        personId,
        authentication,
        dueDate,
        borrowedAt
      );
      return ResponseEntity.status(HttpStatus.CREATED).body(createdLoan);
    } catch (MediaNotFoundException | PersonNotFoundException e) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        e.getMessage(),
        e
      );
    }
  }
}
