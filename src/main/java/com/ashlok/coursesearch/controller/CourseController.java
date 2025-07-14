package com.ashlok.coursesearch.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ashlok.coursesearch.dto.CourseResponse;
import com.ashlok.coursesearch.dto.SearchRequest;
import com.ashlok.coursesearch.dto.SuggestResponse;
import com.ashlok.coursesearch.service.CourseSearchService;
import com.ashlok.coursesearch.service.CourseSuggestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Course Search", description = "Endpoints for searching courses")
public class CourseController {

  private final CourseSearchService searchService;
  private final CourseSuggestService suggestService;

  @GetMapping
  @Operation(summary = "Search courses with filters, sorting and pagination")
  public ResponseEntity<CourseResponse> search(
      @Parameter(description = "Search keyword for title and description") @RequestParam(required = false) String q,
      @Parameter(description = "Minimum age") @RequestParam(required = false) Integer minAge,
      @Parameter(description = "Maximum age") @RequestParam(required = false) Integer maxAge,
      @Parameter(description = "Category filter") @RequestParam(required = false) String category,
      @Parameter(description = "Type filter (ONE_TIME, COURSE, CLUB)") @RequestParam(required = false) String type,
      @Parameter(description = "Minimum price") @RequestParam(required = false) Double minPrice,
      @Parameter(description = "Maximum price") @RequestParam(required = false) Double maxPrice,
      @Parameter(description = "Start date (ISO-8601)") @RequestParam(required = false) String startDate,
      @Parameter(description = "Sort order (upcoming, priceAsc, priceDesc)") @RequestParam(defaultValue = "upcoming") String sort,
      @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {

    SearchRequest request = SearchRequest.builder()
        .q(q).minAge(minAge).maxAge(maxAge).category(category).type(type).minPrice(minPrice).maxPrice(maxPrice)
        .startDate(startDate).sort(sort).page(page).size(size).build();

    CourseResponse response = this.searchService.searchCourses(request);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/suggest")
  @Operation(summary = "Get autocomplete suggestions")
  public ResponseEntity<SuggestResponse> suggest(
      @Parameter(description = "Partial title to suggest") @RequestParam String q) {
    SuggestResponse response = this.suggestService.suggestCourses(q);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

}
