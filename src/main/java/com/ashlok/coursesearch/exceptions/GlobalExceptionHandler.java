package com.ashlok.coursesearch.exceptions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.ashlok.coursesearch.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
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

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
      HttpServletRequest request) {
    List<String> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.toList());
    String errorMessage = String.join("; ", errors);
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .code("INVALID_REQUEST")
        .message(errorMessage)
        .details(errorMessage)
        .path(request.getRequestURI())
        .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex,
      HttpServletRequest request) {
    String message = ex.getMessage();
    String code = "INVALID_REQUEST";
    if (message.startsWith("INVALID_QUERY: ")) {
      code = "INVALID_QUERY";
      message = message.substring("INVALID_QUERY: ".length());
    }
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .code(code)
        .message(message)
        .details(message)
        .path(request.getRequestURI())
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }
}
