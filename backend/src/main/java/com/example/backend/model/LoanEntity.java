package com.example.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loan")
public class LoanEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long loanId;

  @ManyToOne
  @JoinColumn(name = "person_id", nullable = false)
  private PersonEntity person;

  @ManyToOne
  @JoinColumn(name = "media_id", nullable = false)
  private MediaEntity media;

  @Column(name = "borrowed_at", nullable = false, updatable = false)
  private LocalDateTime borrowedAt;

  @Column(name = "returned_at")
  private LocalDateTime returnedAt;

  @Column(name = "due_date")
  private LocalDate dueDate;
}
