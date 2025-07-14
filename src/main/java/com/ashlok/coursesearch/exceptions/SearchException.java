package com.ashlok.coursesearch.exceptions;

import java.io.IOException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchException extends RuntimeException {
  private final String errorCode;

  public SearchException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public SearchException(String errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public SearchException(String errorCode, IOException e) {
    this.errorCode = "";
  }
}
