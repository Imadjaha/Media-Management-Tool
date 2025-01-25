package com.example.backend.dto;

import java.time.LocalDateTime;

public interface MediaWithCategoriesProjection {
  Long getMediaId();
  Long getUserId();
  String getProducer();
  String getTitle();
  String getMediaState();
  String getType();
  Integer getReleaseYear();
  String getNotes();
  String getIsbn();
  Boolean getIsFavorite();
  LocalDateTime getCreatedAt();
  String getCategoryPairs();
}
