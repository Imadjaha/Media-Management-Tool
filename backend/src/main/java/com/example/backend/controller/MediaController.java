package com.example.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.backend.dto.MediaCreationDTO;
import com.example.backend.dto.MediaWithCategoriesDTO;
import com.example.backend.model.MediaEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.service.MediaService;
import com.example.backend.service.UserService;

@RestController
@RequestMapping("/api/media")
public class MediaController {

  private final MediaService mediaService;

  @Autowired
  private UserService userService;

  public MediaController(MediaService mediaService) {
    this.mediaService = mediaService;
  }

  @GetMapping("/seed-media")
  public String loadTestMedia() {
    mediaService.seedMedia(2000);
    return "Seeded 1000 Media records.";
  }

  @GetMapping("/by-username")
  public List<MediaWithCategoriesDTO> getAllMediaByUsernameTest(
    Authentication authentication
  ) {
    String username = authentication.getName();
    return mediaService.getAllMediaByUsernameWithCategories(username);
  }

  @PostMapping
  public MediaWithCategoriesDTO createMedia(
    @RequestBody MediaCreationDTO media,
    Authentication authentication
  ) {
    return mediaService.createMedia(media, authentication);
  }

  @PutMapping("/{mediaId}")
  public MediaEntity updateMedia(
    @PathVariable Long mediaId,
    @RequestBody MediaCreationDTO dto,
    Authentication authentication
  ) {
    return mediaService.updateMedia(mediaId, dto, authentication);
  }

  @PutMapping("/{mediaId}/favorite")
  public ResponseEntity<MediaWithCategoriesDTO> addFavorite(
    @PathVariable Long mediaId,
    @RequestBody MediaEntity add,
    Authentication authentication
  ) {
    String username = authentication.getName();
    Optional<UserEntity> userOptional = userService.getUserByUsername(username);

    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }

    UserEntity user = userOptional.get();
    add.setUser(user);

    MediaWithCategoriesDTO updatedFavorite = mediaService.addToFavorite(
      mediaId,
      add,
      authentication
    );
    return ResponseEntity.ok(updatedFavorite);
  }

  @PostMapping("/{mediaId}/assign-category/{categoryId}")
  public MediaEntity assignCategoryToMedia(
    @PathVariable Long mediaId,
    @PathVariable Long categoryId,
    Authentication authentication
  ) {
    return mediaService.assignCategoryToMedia(
      mediaId,
      categoryId,
      authentication
    );
  }

  @DeleteMapping("/{mediaId}/remove-category/{categoryId}")
  public void removeCategoryFromMedia(
    @PathVariable Long mediaId,
    @PathVariable Long categoryId,
    Authentication authentication
  ) {
    mediaService.removeCategoryFromMedia(mediaId, categoryId, authentication);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMedia(@PathVariable Long id) {
    mediaService.deleteMedia(id);
    return ResponseEntity.noContent().build();
  }
}
