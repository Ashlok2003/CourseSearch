package com.ashlok.coursesearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ashlok.coursesearch.model.CourseDocument;

public interface CourseRepository extends ElasticsearchRepository<CourseDocument, String> {

}
