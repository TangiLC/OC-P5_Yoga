package com.openclassrooms.starterjwt.payload.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SuiteDisplayName("PAYLOAD")
@DisplayName("Unit tests for JwtResponse")
class JwtResponseUnitTest {

  @DisplayName("Should handle different JwtResponse Create scenario ")
  @ParameterizedTest(name = "({index}) : {0}")
  @CsvSource(
    {
      "Valid values, valid-token, 1, john.doe, John, Doe, true",
      "Empty strings for text fields, valid-token, 1, '', '', '', false",
      "Null fields for text fields, valid-token, 1, , , , ",
    }
  )
  void should_create_jwt_response_with_expected_values(
    String scenarioName,
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

  @DisplayName("Should handle different JwtResponse Update scenarios")
  @ParameterizedTest(name = "({index}) : {0}")
  @CsvSource(
    {
      "Update all fields, new-token, NewType, 2, jane.doe, Jane, Doe, true",
      "Empty strings in setters, '', '', 999, '', '', '', false",
      "Null fields in setters, , , , , , , ",
    }
  )
  void should_update_jwt_response_fields(
    String scenarioName,
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
}
