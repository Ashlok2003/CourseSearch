package com.ashlok.coursesearch.dto;

import java.util.List;

import com.ashlok.coursesearch.model.CourseDocument;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseResponse {

  private long total;

  private List<CourseDocument> courses;
}
