package com.openclassrooms.starterjwt.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

class NotFoundExceptionTest {

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName(
    "Should correctly instantiate NotFoundException with default message"
  )
  void testDefaultMessage() {
    NotFoundException exception = new NotFoundException();

    assertThrows(
      NotFoundException.class,
      () -> {
        throw exception;
      }
    );
    assertEquals(
      "Not found: the data you are looking for is unavailable.",
      exception.getMessage()
    );
  }

  @Test
  @DisplayName(
    "Should correctly instantiate NotFoundException with custom message"
  )
  void testCustomMessage() {
    String customMessage = "This is another custom not found message";

    NotFoundException exception = new NotFoundException(customMessage);

    assertThrows(
      NotFoundException.class,
      () -> {
        throw exception;
      }
    );
    assertEquals(customMessage, exception.getMessage());
  }
}
