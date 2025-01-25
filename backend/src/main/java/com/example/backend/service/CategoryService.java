// package com.example.backend.service;
package com.example.backend.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.model.CategoryEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.UserRepository;

@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;

  public CategoryService(
    CategoryRepository categoryRepository,
    UserRepository userRepository
  ) {
    this.categoryRepository = categoryRepository;
    this.userRepository = userRepository;
  }

  public CategoryEntity createCategory(
    CategoryDTO categoryDTO,
    Authentication authentication
  ) {
    String username = authentication.getName();

    UserEntity user = userRepository
      .findByUsername(username)
      .orElseThrow(() ->
        new IllegalArgumentException("User not found: " + username)
      );

    CategoryEntity category = new CategoryEntity();
    category.setCategoryName(categoryDTO.getCategoryName());
    category.setUser(user);
    return categoryRepository.save(category);
  }

  public CategoryEntity updateCategory(
    Long categoryId,
    CategoryEntity categoryDetails
  ) {
    CategoryEntity category = categoryRepository
      .findById(categoryId)
      .orElseThrow(() -> new RuntimeException("Category not found"));
    category.setCategoryName(categoryDetails.getCategoryName());
    return categoryRepository.save(category);
  }

  public void deleteCategory(Long categoryId) {
    categoryRepository.deleteById(categoryId);
  }

  public List<CategoryDTO> getCategoryDTOsByUsername(String username) {
    return categoryRepository.findCategoryDTOsByUsername(username);
  }
}
