// package com.example.backend.dto;
package com.example.backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class CategoryDTO {

  private Long categoryId;
  private String categoryName;

  public CategoryDTO() {}

  public CategoryDTO(Long categoryId, String categoryName) {
    this.categoryId = categoryId;
    this.categoryName = categoryName;
  }
}
