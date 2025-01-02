package com.openclassrooms.starterjwt.payload.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import org.junit.platform.suite.api.SuiteDisplayName;

@SuiteDisplayName("PAYLOAD")
@DisplayName("Unit tests for LoginRequest")
class LoginRequestUnitTest {

 
  @ParameterizedTest(name = "({index}) : {0}")
  @CsvSource(
    {
      "Valid email and password, test@test.com, password123",
      "Empty email only, '', password123",
      "Empty password only, test@test.com, ''",
      "Empty email and password, '', ''",
      "Blank email and password, '   ', '   '",
      "Null email only, , password123",
      "Null password only, test@test.com, ",
      "Null email and password, , ",
      
    }
  )
  @DisplayName("Should handle different LoginRequest scenario ")
  void should_handle_login_request_operations(
    String scenarioName,
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
