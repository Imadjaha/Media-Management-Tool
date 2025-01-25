package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MediaWithCategoriesDTO {

  private Long mediaId;
  private Long userId;

  private String producer;
  private String title;
  private String mediaState;
  private String type;
  private Integer releaseYear;
  private String notes;
  private String isbn;
  private Boolean isFavorite;
  private LocalDateTime createdAt;

  private List<CategoryDTO> categories = new ArrayList<>();

  public MediaWithCategoriesDTO() {}

  public MediaWithCategoriesDTO(MediaCreationDTO media) {
    this.producer = media.getProducer();
    this.title = media.getTitle();
    this.mediaState = media.getMediaState();
    this.type = media.getType();
    this.releaseYear = media.getReleaseYear();
    this.notes = media.getNotes();
    this.isbn = media.getIsbn();
    this.isFavorite = media.getIsFavorite();
    this.createdAt = media.getCreatedAt();
  }

  public static MediaWithCategoriesDTO fromProjection(
    MediaWithCategoriesProjection projection
  ) {
    MediaWithCategoriesDTO dto = new MediaWithCategoriesDTO();
    dto.setMediaId(projection.getMediaId());
    dto.setUserId(projection.getUserId());
    dto.setProducer(projection.getProducer());
    dto.setTitle(projection.getTitle());
    dto.setMediaState(projection.getMediaState());
    dto.setType(projection.getType());
    dto.setReleaseYear(projection.getReleaseYear());
    dto.setNotes(projection.getNotes());
    dto.setIsbn(projection.getIsbn());
    dto.setIsFavorite(projection.getIsFavorite());
    dto.setCreatedAt(projection.getCreatedAt());

    if (projection.getCategoryPairs() != null) {
      String[] pairs = projection.getCategoryPairs().split(",");
      for (String pair : pairs) {
        String[] parts = pair.split(":");
        if (parts.length == 2) {
          Long catId = Long.valueOf(parts[0]);
          String catName = parts[1];
          CategoryDTO categoryDTO = new CategoryDTO();
          categoryDTO.setCategoryId(catId);
          categoryDTO.setCategoryName(catName);
          dto.getCategories().add(categoryDTO);
        }
      }
    }
    return dto;
  }
}
