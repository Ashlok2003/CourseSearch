package com.ashlok.coursesearch.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchRequest {

  @Size(max = 100, message = "Search query cannot exceed 100 characters")
  private String q;

  @Min(value = 0, message = "Minimum age cannot be negative")
  private Integer minAge;

  @Min(value = 0, message = "Maximum age cannot be negative")
  private Integer maxAge;

  @Size(max = 50, message = "Category cannot exceed 50 characters")
  private String category;

  @Pattern(regexp = "ONE_TIME|COURSE|CLUB", message = "Type must be ONE_TIME, COURSE, or CLUB")
  private String type;

  @Min(value = 0, message = "Minimum price cannot be negative")
  private Double minPrice;

  @Min(value = 0, message = "Maximum price cannot be negative")
  private Double maxPrice;

  @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z", message = "Start date must be in ISO-8601 format (e.g., 2025-07-01T00:00:00Z)")
  private String startDate;

  @Pattern(regexp = "upcoming|priceAsc|priceDesc", message = "Sort must be upcoming, priceAsc, or priceDesc")
  private String sort;

  @Min(value = 0, message = "Page number cannot be negative")
  @Builder.Default
  private int page = 0;

  @Min(value = 1, message = "Page size must be at least 1")
  @Max(value = 100, message = "Page size cannot exceed 100")
  @Builder.Default
  private int size = 10;

  @AssertTrue(message = "minAge must be less than or equal to maxAge")
  public boolean isValidAgeRange() {
    if (minAge == null || maxAge == null) {
      return true;
    }
    return minAge <= maxAge;
  }

  @AssertTrue(message = "minPrice must be less than or equal to maxPrice")
  public boolean isValidPriceRange() {
    if (minPrice == null || maxPrice == null) {
      return true;
    }
    return minPrice <= maxPrice;
  }
}
