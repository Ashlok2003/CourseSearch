package com.ashlok.coursesearch.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.ashlok.coursesearch.dto.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(SearchException.class)
  public ResponseEntity<ErrorResponse> handleSearchException(SearchException ex, WebRequest request) {
    ErrorResponse response = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .code(ex.getErrorCode())
        .message(ex.getMessage())
        .details(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    log.error("Search error occurred: {}", response);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
    ErrorResponse response = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
        .code("INTERNAL_ERROR")
        .message("An unexpected error occurred")
        .details(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    log.error("Unexpected error occurred: {}", response, ex);
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
