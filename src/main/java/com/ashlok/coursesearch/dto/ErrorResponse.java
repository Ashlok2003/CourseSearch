package com.ashlok.coursesearch.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String code;

  private String message;
  private String details;
  private String path;
}
