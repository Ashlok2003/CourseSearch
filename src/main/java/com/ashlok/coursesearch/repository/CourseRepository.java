package com.ashlok.coursesearch.repository;

import com.ashlok.coursesearch.model.CourseDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CourseRepository extends ElasticsearchRepository<CourseDocument, String> {

}
