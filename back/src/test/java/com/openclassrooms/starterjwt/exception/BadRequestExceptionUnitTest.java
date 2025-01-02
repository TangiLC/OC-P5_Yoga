package com.openclassrooms.starterjwt.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuiteDisplayName("EXCEPTION")
@DisplayName("Unit tests for BadRequestException")
class BadRequestExceptionUnitTest {

  private static final String DEFAULT_MESSAGE =
    "Bad request: the provided data is invalid, operation is not allowed.";

  @Test
  @DisplayName(
    "Should create exception with default message using default constructor"
  )
  void should_create_BadRequest_exception_with_default_constructor() {
    BadRequestException exception = new BadRequestException();

    assertThat(exception)
      .isInstanceOf(BadRequestException.class)
      .isInstanceOf(RuntimeException.class)
      .hasMessage(DEFAULT_MESSAGE);

    ResponseStatus responseStatus =
      BadRequestException.class.getAnnotation(ResponseStatus.class);
    assertThat(responseStatus)
      .isNotNull()
      .extracting(ResponseStatus::value)
      .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  @DisplayName(
    "Should create exception with custom message using message constructor"
  )
  void should_create_BadRequest_exception_with_message_constructor() {
    String customMessage = "This is a custom error message";

    BadRequestException exception = new BadRequestException(customMessage);

    assertThat(exception)
      .isInstanceOf(BadRequestException.class)
      .isInstanceOf(RuntimeException.class)
      .hasMessage(customMessage);

    ResponseStatus responseStatus =
      BadRequestException.class.getAnnotation(ResponseStatus.class);
    assertThat(responseStatus)
      .isNotNull()
      .extracting(ResponseStatus::value)
      .isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
