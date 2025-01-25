package com.example.backend.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "media")
public class MediaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long mediaId;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Column(name = "producer")
  private String producer;

  @Column(nullable = false)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(name = "media_state", nullable = false)
  private MediaState mediaState;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MediaType type;

  @Column(name = "release_year")
  private Integer releaseYear;

  @Lob
  @Column(name = "notes")
  private String notes; // indicates that the property should be stored in the database in the form of a large object type in the database.

  @Column(name = "isbn")
  private String isbn;

  @Column(name = "is_favorite", nullable = false)
  private Boolean isFavorite = false;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @OneToMany(
    mappedBy = "media",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  @JsonIgnore
  private Set<MediaCategory> mediaCategories = new HashSet<>();

  @OneToMany(
    mappedBy = "media",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  @JsonIgnore
  private Set<LoanEntity> loans = new HashSet<>();

  // Method to get categories directly
  @Transient
  public Set<CategoryEntity> getCategories() {
    Set<CategoryEntity> categories = new HashSet<>();
    for (MediaCategory mediaCategory : mediaCategories) {
      categories.add(mediaCategory.getCategory());
    }
    return categories;
  }


}
