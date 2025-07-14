package com.ashlok.coursesearch.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ashlok.coursesearch.dto.SuggestResponse;
import com.ashlok.coursesearch.exceptions.SearchException;
import com.ashlok.coursesearch.model.CourseDocument;

import co.elastic.clients.elasticsearch.core.search.CompletionSuggester;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseSuggestService {

  private final ElasticsearchOperations operations;

  private static final int MAX_SUGGEST_SIZE = 10;
  private static final int MIN_QUERY_LENGTH = 1;
  private static final int MAX_QUERY_LENGTH = 100;
  private static final String FUZZY_FUZZINESS = "AUTO";

  public SuggestResponse suggestCourses(String partialTitle) {

    validatePartialTitle(partialTitle);

    try {

      CompletionSuggester completionSuggester = new CompletionSuggester.Builder()
          .field("suggest")
          .size(MAX_SUGGEST_SIZE)
          .fuzzy(f -> f.fuzziness(FUZZY_FUZZINESS))
          .skipDuplicates(true)
          .build();

      FieldSuggester fieldSuggester = new FieldSuggester.Builder()
          .completion(completionSuggester)
          .build();

      Suggester suggester = new Suggester.Builder()
          .text(partialTitle.trim())
          .suggesters(Collections.singletonMap("course-suggest", fieldSuggester))
          .build();

      NativeQuery query = new NativeQueryBuilder()
          .withSuggester(suggester)
          .build();

      log.debug("Executing suggestion query for partial title: {}", partialTitle);

      SearchHits<CourseDocument> searchHits = operations.search(query, CourseDocument.class);

      var suggest = searchHits.getSuggest();
      return this.processSuggestResponse(suggest);

    } catch (Exception e) {
      log.error("Failed to fetch suggestions for partial title: {}", partialTitle, e);
      throw new SearchException("SUGGESTION_ERROR", "Failed to retrieve suggestions", e);
    }
  }

  private SuggestResponse processSuggestResponse(Suggest suggest) {
    if (suggest == null) {
      return SuggestResponse.builder().suggestions(Collections.emptyList()).build();
    }

    var courseSuggest = suggest.getSuggestion("course-suggest");
    if (courseSuggest == null) {
      return SuggestResponse.builder().suggestions(Collections.emptyList()).build();
    }

    List<String> suggestions = courseSuggest.getEntries().stream()
        .flatMap(entry -> entry.getOptions().stream())
        .map(option -> option.getText().stripLeading())
        .filter(StringUtils::hasText)
        .distinct()
        .limit(MAX_SUGGEST_SIZE)
        .collect(Collectors.toList());

    return SuggestResponse.builder().suggestions(suggestions).build();
  }

  private void validatePartialTitle(String partialTitle) {
    if (!StringUtils.hasText(partialTitle)) {
      log.warn("Invalid partial title: empty or null");
      throw new SearchException("INVALID_QUERY", "Partial title cannot be empty or null");
    }

    if (partialTitle.length() < MIN_QUERY_LENGTH) {
      log.warn("Partial title too short: {}", partialTitle);
      throw new SearchException("INVALID_QUERY",
          "Partial title must be at least " + MIN_QUERY_LENGTH + " characters");
    }

    if (partialTitle.length() > MAX_QUERY_LENGTH) {
      log.warn("Partial title too long: {}", partialTitle);
      throw new SearchException("INVALID_QUERY",
          "Partial title exceeds maximum length of " + MAX_QUERY_LENGTH + " characters");
    }
  }
}
