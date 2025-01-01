package com.openclassrooms.starterjwt.payload.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LoginRequestUnitTest {

  @DisplayName("Should handle LoginRequest scenarios")
  @ParameterizedTest(name = "{0}")
  @CsvSource(
    {
      "Valid email and password, test@test.com, password123",
      "Null email and password, null, null",
      "Empty email and password, '', ''",
      "Blank email and password, '   ', '   '",
      "Null email only, null, password123",
      "Null password only, test@test.com, null",
      "Empty email only, '', password123",
      "Empty password only, test@test.com, ''",
    }
  )
  void should_handle_login_request_operations(
    String testName,
    String email,
    String password
  ) {
    LoginRequest request = new LoginRequest();

    request.setEmail(email);
    request.setPassword(password);

    assertThat(request)
      .satisfies(r -> {
        assertThat(r.getEmail()).isEqualTo(email);
        assertThat(r.getPassword()).isEqualTo(password);
      });
  }
}
