package com.ashlok.coursesearch.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuggestResponse {
  private List<String> suggestions;
}
