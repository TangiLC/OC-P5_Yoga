package com.openclassrooms.starterjwt.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

class BadRequestExceptionTest {

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName(
    "Should correctly instantiate BadRequestException with default message"
  )
  void testDefaultMessage() {
    BadRequestException exception = new BadRequestException();

    assertThrows(
      BadRequestException.class,
      () -> {
        throw exception;
      }
    );
    assertEquals(
      "Bad request: the provided data is invalid, operation is not allowed.",
      exception.getMessage()
    );
  }

  @Test
  @DisplayName(
    "Should correctly instantiate BadRequestException with custom message"
  )
  void testCustomMessage() {
    String customMessage = "This is another custom bad request message";

    BadRequestException exception = new BadRequestException(customMessage);

    assertThrows(
      BadRequestException.class,
      () -> {
        throw exception;
      }
    );
    assertEquals(customMessage, exception.getMessage());
  }
}
