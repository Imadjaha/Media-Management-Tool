// package com.example.backend.controller;
package com.example.backend.controller;

import java.util.List;

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

import com.example.backend.dto.CategoryDTO;
import com.example.backend.model.CategoryEntity;
import com.example.backend.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @PostMapping
  public CategoryEntity createCategory(
    @RequestBody CategoryDTO categoryDTO,
    Authentication authentication
  ) {
    return categoryService.createCategory(categoryDTO, authentication);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryEntity> updateCategory(
    @PathVariable Long id,
    @RequestBody CategoryEntity category
  ) {
    return ResponseEntity.ok(categoryService.updateCategory(id, category));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/user/dto")
  public List<CategoryDTO> getCategoryDTOsForUser(
    Authentication authentication
  ) {
    String username = authentication.getName();
    return categoryService.getCategoryDTOsByUsername(username);
  }
}
