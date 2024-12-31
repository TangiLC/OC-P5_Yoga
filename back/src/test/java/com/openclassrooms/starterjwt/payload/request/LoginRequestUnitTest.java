package com.openclassrooms.starterjwt.payload.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LoginRequestUnitTest {

  @DisplayName("Should handle LoginRequest scenarios")
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideLoginRequestScenarios")
  void should_handle_login_request_operations(
    String testName,
    String email,
    String password,
    boolean shouldHaveViolations,
    int expectedViolations
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

  private static Stream<Arguments> provideLoginRequestScenarios() {
    return Stream.of(
      Arguments.of(
        "Should accept valid email and password",
        "test@test.com",
        "password123",
        false,
        0
      ),
      Arguments.of(
        "Should reject null email and password",
        null,
        null,
        true,
        2
      ),
      Arguments.of("Should reject empty email and password", "", "", true, 2),
      Arguments.of(
        "Should reject blank email and password",
        "   ",
        "   ",
        true,
        2
      ),
      Arguments.of(
        "Should reject null email only",
        null,
        "password123",
        true,
        1
      ),
      Arguments.of(
        "Should reject null password only",
        "test@test.com",
        null,
        true,
        1
      ),
      Arguments.of(
        "Should reject empty email only",
        "",
        "password123",
        true,
        1
      ),
      Arguments.of(
        "Should reject empty password only",
        "test@test.com",
        "",
        true,
        1
      )
    );
  }
}
