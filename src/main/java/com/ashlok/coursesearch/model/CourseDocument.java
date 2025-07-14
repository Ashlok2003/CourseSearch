package com.ashlok.coursesearch.model;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "courses")
@Setting(settingPath = "/elasticsearch/settings.json")
@Mapping(mappingPath = "/elasticsearch/mapping.json")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseDocument {
  @Id
  private String id;

  @Field(type = FieldType.Text, analyzer = "fuzzy_analyzer", searchAnalyzer = "fuzzy_analyzer")
  private String title;

  @Field(type = FieldType.Text, analyzer = "standard")
  private String description;

  @Field(type = FieldType.Keyword)
  private String category;

  @Field(type = FieldType.Keyword)
  private String type;

  @Field(type = FieldType.Keyword)
  private String gradeRange;

  @Field(type = FieldType.Integer)
  private Integer minAge;

  @Field(type = FieldType.Integer)
  private Integer maxAge;

  @Field(type = FieldType.Double)
  private Double price;

  @Field(type = FieldType.Date, format = DateFormat.date_time)
  private OffsetDateTime nextSessionDate;

  @Field(type = FieldType.Text)
  private String suggest;
}
