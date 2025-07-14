package com.ashlok.coursesearch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ashlok.coursesearch.dto.CourseResponse;
import com.ashlok.coursesearch.dto.SearchRequest;
import com.ashlok.coursesearch.exceptions.SearchException;
import com.ashlok.coursesearch.model.CourseDocument;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseSearchService {

  private static final int MAX_RESULT_WINDOW = 10000;
  private static final int FUZZY_MAX_EXPANSIONS = 50;
  private static final int MAX_QUERY_LENGTH = 100;
  private final ElasticsearchOperations operations;

  public CourseResponse searchCourses(@Valid SearchRequest request) {
    validateSearchRequest(request);

    try {

      Query query = buildSearchQuery(request.getQ());
      List<Query> filters = buildFilters(request);
      NativeQuery nativeQuery = buildNativeQuery(request, query, filters);

      log.debug("Executing Elasticsearch query: {}", nativeQuery.getQuery());

      SearchHits<CourseDocument> searchHits = operations.search(nativeQuery, CourseDocument.class);
      List<CourseDocument> courses = searchHits.getSearchHits().stream()
          .map(SearchHit::getContent)
          .collect(Collectors.toList());

      log.info("Found {} courses for query: {}", searchHits.getTotalHits(), request.getQ());
      return CourseResponse.builder()
          .total(searchHits.getTotalHits())
          .courses(courses)
          .build();

    } catch (Exception e) {
      log.error("Search failed for request: {}", request, e);
      throw new SearchException("SEARCH_ERROR", "Failed to execute search: " + e.getMessage(), e);
    }
  }

  private void validateSearchRequest(SearchRequest request) {
    if (request.getPage() * request.getSize() > MAX_RESULT_WINDOW) {
      throw new SearchException("INVALID_QUERY",
          "Result window is too large. Maximum allowed: " + MAX_RESULT_WINDOW);
    }
    if (StringUtils.hasText(request.getQ()) && request.getQ().length() > MAX_QUERY_LENGTH) {
      throw new SearchException("INVALID_QUERY",
          "Search term exceeds maximum length of " + MAX_QUERY_LENGTH);
    }
  }

  private List<Query> buildFilters(SearchRequest request) {
    List<Query> filters = new ArrayList<>();

    if (request.getCategory() != null && !request.getCategory().isEmpty()) {
      filters.add(Query.of(q -> q.term(t -> t.field("category").value(request.getCategory()))));
    }
    if (request.getType() != null && !request.getType().isEmpty()) {
      filters.add(Query.of(q -> q.term(t -> t.field("type").value(request.getType()))));
    }
    if (request.getMinAge() != null) {
      filters.add(Query.of(q -> q.range(r -> r.number(n -> n.field("minAge").gte(request.getMinAge().doubleValue())))));
    }
    if (request.getMaxAge() != null) {
      filters.add(Query.of(q -> q.range(r -> r.number(n -> n.field("maxAge").lte(request.getMaxAge().doubleValue())))));
    }
    if (request.getMinPrice() != null) {
      filters.add(Query.of(q -> q.range(r -> r.number(n -> n.field("price").gte(request.getMinPrice())))));
    }
    if (request.getMaxPrice() != null) {
      filters.add(Query.of(q -> q.range(r -> r.number(n -> n.field("price").lte(request.getMaxPrice())))));
    }
    if (request.getStartDate() != null && !request.getStartDate().isEmpty()) {
      filters.add(Query.of(q -> q.range(r -> r.date(d -> d.field("nextSessionDate").gte(request.getStartDate())))));
    }

    return filters;
  }

  private NativeQuery buildNativeQuery(SearchRequest request, Query query, List<Query> filters) {
    String sortField = "nextSessionDate";
    Sort.Direction sortDirection = Sort.Direction.ASC;

    if (request.getSort() != null) {
      switch (request.getSort().toLowerCase()) {
        case "priceasc":
          sortField = "price";
          sortDirection = Sort.Direction.ASC;
          break;
        case "pricedesc":
          sortField = "price";
          sortDirection = Sort.Direction.DESC;
          break;
        case "upcoming":
        default:
          break;
      }
    }

    NativeQueryBuilder queryBuilder = NativeQuery.builder()
        .withQuery(q -> q.bool(b -> {
          b.must(query);
          if (!filters.isEmpty()) {
            b.filter(filters);
          }
          return b;
        }))
        .withSort(Sort.by(sortDirection, sortField))
        .withPageable(PageRequest.of(request.getPage(), request.getSize()));

    return queryBuilder.build();
  }

  private Query buildSearchQuery(String searchTerm) {
    return StringUtils.hasText(searchTerm)
        ? Query.of(q -> q
            .bool(b -> b
                .should(s -> s
                    .fuzzy(f -> f
                        .field("title")
                        .value(searchTerm.toLowerCase())
                        .fuzziness("2")
                        .prefixLength(0)
                        .maxExpansions(FUZZY_MAX_EXPANSIONS)
                        .transpositions(true)
                        .boost(2.0f)))
                .should(s -> s
                    .match(m -> m
                        .field("title.ngram")
                        .query(searchTerm.toLowerCase())
                        .minimumShouldMatch("50%")
                        .boost(1.0f)))
                .minimumShouldMatch("1")))
        : Query.of(q -> q.matchAll(m -> m));
  }
}
