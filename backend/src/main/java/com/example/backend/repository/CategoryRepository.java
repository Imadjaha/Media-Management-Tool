// package com.example.backend.repository;
package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.model.CategoryEntity;

@Repository
public interface CategoryRepository
  extends JpaRepository<CategoryEntity, Long> {
  List<CategoryEntity> findByCategoryName(String categoryName);

  @Query(
    "SELECT new com.example.backend.dto.CategoryDTO(c.categoryId, c.categoryName) " +
    "FROM CategoryEntity c " +
    "WHERE c.user.username = :username"
  )
  List<CategoryDTO> findCategoryDTOsByUsername(String username);
}
