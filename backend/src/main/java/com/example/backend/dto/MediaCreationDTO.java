package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaCreationDTO {

  private String producer;
  private String title;
  private String mediaState;
  private String type;
  private Integer releaseYear;
  private String notes;
  private String isbn;
  private Boolean isFavorite;
  private LocalDateTime createdAt;
  private List<Long> categories = new ArrayList<>();
}
