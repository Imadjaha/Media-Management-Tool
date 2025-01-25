package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.MediaCategory;

@Repository
public interface MediaCategoryRepository
  extends JpaRepository<MediaCategory, Long> {
  List<MediaCategory> findByCategory_CategoryId(Long categoryId);
  boolean existsByMediaMediaIdAndCategoryCategoryId(
    Long mediaId,
    Long categoryId
  );
  Optional<MediaCategory> findByMediaMediaIdAndCategoryCategoryId(
    Long mediaId,
    Long categoryId
  );
}
