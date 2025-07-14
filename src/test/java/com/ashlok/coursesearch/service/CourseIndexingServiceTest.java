package com.ashlok.coursesearch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.ashlok.coursesearch.config.BaseElasticsearchTest;
import com.ashlok.coursesearch.model.CourseDocument;
import com.ashlok.coursesearch.repository.CourseRepository;

@SpringBootTest
@ActiveProfiles("dev")
class CourseIndexingServiceTest extends BaseElasticsearchTest {

  @Autowired
  private CourseIndexingService indexingService;

  @Autowired
  private CourseRepository repository;

  @BeforeEach
  void setUp() {
    repository.deleteAll();
  }

  @Test
  void testIndexCourses() throws IOException {
    indexingService.indexSampleData();

    Iterable<CourseDocument> iterable = repository.findAll();

    List<CourseDocument> courses = StreamSupport
        .stream(iterable.spliterator(), false)
        .collect(Collectors.toList());

    assertEquals(5, courses.size());

    assertEquals("Dinosaurs 101", courses.stream()
        .filter(c -> c.getId().equals("1"))
        .findFirst()
        .orElseThrow()
        .getTitle());
  }
}
