package com.ashlok.coursesearch.service;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.ashlok.coursesearch.model.CourseDocument;
import com.ashlok.coursesearch.repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseIndexingService {

  private final CourseRepository repository;
  private final ObjectMapper mapper;

  @PostConstruct
  public void indexSampleData() throws IOException {
    try {
      ClassPathResource resource = new ClassPathResource("sample-courses.json");

      List<CourseDocument> courses = mapper.readValue(
          resource.getInputStream(),
          new TypeReference<List<CourseDocument>>() {
          });

      this.repository.saveAll(courses);
    } catch (Exception e) {
      throw new IOException("Failed to index sample data: " + e.getMessage(), e);
    }
  }

}
