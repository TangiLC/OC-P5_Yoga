package com.openclassrooms.starterjwt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

  public BadRequestException() {
    super(
      "Bad request: the provided data is invalid, operation is not allowed."
    );
  }

  public BadRequestException(String message) {
    super(message);
  }
}
