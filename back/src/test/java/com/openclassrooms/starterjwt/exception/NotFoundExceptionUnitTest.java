package com.openclassrooms.starterjwt.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

class NotFoundExceptionUnitTest {

  private static final String DEFAULT_MESSAGE =
    "Not found: the data you are looking for is unavailable.";

  @Test
  @DisplayName(
    "Should create exception with default message using default constructor"
  )
  void should_create_NotFound_exception_with_default_constructor() {
    NotFoundException exception = new NotFoundException();

    assertThat(exception)
      .isInstanceOf(NotFoundException.class)
      .isInstanceOf(RuntimeException.class)
      .hasMessage(DEFAULT_MESSAGE);

    ResponseStatus responseStatus =
      NotFoundException.class.getAnnotation(ResponseStatus.class);
    assertThat(responseStatus)
      .isNotNull()
      .extracting(ResponseStatus::value)
      .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName(
    "Should create exception with custom message using message constructor"
  )
  void should_create_NotFound_exception_with_message_constructor() {
    String customMessage = "This is a custom not found message";

    NotFoundException exception = new NotFoundException(customMessage);

    assertThat(exception)
      .isInstanceOf(NotFoundException.class)
      .isInstanceOf(RuntimeException.class)
      .hasMessage(customMessage);

    // And check HTTP status
    ResponseStatus responseStatus =
      NotFoundException.class.getAnnotation(ResponseStatus.class);
    assertThat(responseStatus)
      .isNotNull()
      .extracting(ResponseStatus::value)
      .isEqualTo(HttpStatus.NOT_FOUND);
  }
}
