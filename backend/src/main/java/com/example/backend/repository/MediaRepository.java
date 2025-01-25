package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend.dto.MediaWithCategoriesProjection;
import com.example.backend.model.MediaEntity;
import com.example.backend.model.MediaState;
import com.example.backend.model.MediaType;

@Repository
public interface MediaRepository extends JpaRepository<MediaEntity, Long> {
  @Query(
    value = """
    SELECT 
        m.media_id                     AS mediaId,
        m.user_id                      AS userId,
        m.producer                     AS producer,
        m.title                        AS title,
        m.media_state                  AS mediaState,
        m.type                         AS type,
        m.release_year                 AS releaseYear,
        m.notes                        AS notes,
        m.isbn                         AS isbn,
        m.is_favorite                  AS isFavorite,
        m.created_at                   AS createdAt,
        group_concat(CONCAT(c.category_id, ':', c.category_name) SEPARATOR ',') AS categoryPairs
    FROM media m
    LEFT JOIN media_category mc ON m.media_id = mc.media_id
    LEFT JOIN category c        ON mc.category_id = c.category_id
    WHERE m.user_id = :userId
    GROUP BY 
        m.media_id, m.user_id, m.producer, m.title, 
        m.media_state, m.type, m.release_year, m.notes, 
        m.isbn, m.is_favorite, m.created_at
    """,
    nativeQuery = true
  )
  List<MediaWithCategoriesProjection> findMediaWithCategoriesByUserId(
    Long userId
  );

  List<MediaEntity> findByUserUserId(Long userId);
  List<MediaEntity> findByMediaState(MediaState mediaState);
  List<MediaEntity> findByType(MediaType type);
  List<MediaEntity> findByIsFavorite(Boolean isFavorite);
  Optional<MediaEntity> findByIsbn(String isbn);
}
