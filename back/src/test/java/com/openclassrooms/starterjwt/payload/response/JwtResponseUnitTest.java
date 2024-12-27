package com.openclassrooms.starterjwt.payload.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class JwtResponseTest {

  @DisplayName("Should handle JwtResponse Create scenarios")
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideJwtResponseScenarios")
  void should_create_jwt_response_with_expected_values(
    String testName,
    String token,
    Long id,
    String username,
    String firstName,
    String lastName,
    Boolean admin
  ) {
    JwtResponse response = new JwtResponse(
      token,
      id,
      username,
      firstName,
      lastName,
      admin
    );

    assertThat(response)
      .satisfies(r -> {
        assertThat(r.getToken()).isEqualTo(token);
        assertThat(r.getType()).isEqualTo("Bearer");
        assertThat(r.getId()).isEqualTo(id);
        assertThat(r.getUsername()).isEqualTo(username);
        assertThat(r.getFirstName()).isEqualTo(firstName);
        assertThat(r.getLastName()).isEqualTo(lastName);
        assertThat(r.getAdmin()).isEqualTo(admin);
      });
  }

  @DisplayName("Should handle JwtResponse Update scenarios")
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideJwtResponseModificationScenarios")
  void should_update_jwt_response_fields(
    String testName,
    String newToken,
    String newType,
    Long newId,
    String newUsername,
    String newFirstName,
    String newLastName,
    Boolean newAdmin
  ) {
    JwtResponse response = new JwtResponse(
      "initial-token",
      1L,
      "initial-username",
      "initial-firstName",
      "initial-lastName",
      false
    );

    response.setToken(newToken);
    response.setType(newType);
    response.setId(newId);
    response.setUsername(newUsername);
    response.setFirstName(newFirstName);
    response.setLastName(newLastName);
    response.setAdmin(newAdmin);

    assertThat(response)
      .satisfies(r -> {
        assertThat(r.getToken()).isEqualTo(newToken);
        assertThat(r.getType()).isEqualTo(newType);
        assertThat(r.getId()).isEqualTo(newId);
        assertThat(r.getUsername()).isEqualTo(newUsername);
        assertThat(r.getFirstName()).isEqualTo(newFirstName);
        assertThat(r.getLastName()).isEqualTo(newLastName);
        assertThat(r.getAdmin()).isEqualTo(newAdmin);
      });
  }

  private static Stream<Arguments> provideJwtResponseScenarios() {
    return Stream.of(
      Arguments.of(
        "Should create response with all valid values",
        "valid-token",
        1L,
        "john.doe",
        "John",
        "Doe",
        true
      ),
      Arguments.of(
        "Should handle null values except token and id",
        "valid-token",
        1L,
        null,
        null,
        null,
        null
      ),
      Arguments.of(
        "Should handle empty strings for text fields",
        "valid-token",
        1L,
        "",
        "",
        "",
        false
      )
    );
  }

  private static Stream<Arguments> provideJwtResponseModificationScenarios() {
    return Stream.of(
      Arguments.of(
        "Should update all fields with valid values",
        "new-token",
        "NewType",
        2L,
        "jane.doe",
        "Jane",
        "Doe",
        true
      ),
      Arguments.of(
        "Should handle null values in setters",
        null,
        null,
        null,
        null,
        null,
        null,
        null
      ),
      Arguments.of(
        "Should handle empty strings in setters",
        "",
        "",
        999L,
        "",
        "",
        "",
        false
      )
    );
  }
}
