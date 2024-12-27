package com.openclassrooms.starterjwt.payload.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lombok.var;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SignupRequestUnitTest {

  private static final Validator validator;

  static {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @DisplayName("Should handle validate SignupRequest scenarios")
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideSignupRequestScenarios")
  void should_validate_signup_request(
    String testName,
    String email,
    String firstName,
    String lastName,
    String password,
    boolean shouldHaveViolations,
    int expectedViolations,
    String expectedViolationMessages
  ) {
    SignupRequest request = new SignupRequest();
    request.setEmail(email);
    request.setFirstName(firstName);
    request.setLastName(lastName);
    request.setPassword(password);

    var violations = validator.validate(request);
    String actualViolationMessages = violations
      .stream()
      .map(v -> v.getPropertyPath().toString() + ": " + v.getMessage())
      .collect(Collectors.joining(", "));

    assertThat(request)
      .satisfies(r -> {
        assertThat(r.getEmail()).isEqualTo(email);
        assertThat(r.getFirstName()).isEqualTo(firstName);
        assertThat(r.getLastName()).isEqualTo(lastName);
        assertThat(r.getPassword()).isEqualTo(password);
      });

    assertThat(violations.isEmpty()).isEqualTo(!shouldHaveViolations);
    assertThat(violations).hasSize(expectedViolations);

    if (!violations.isEmpty()) {
      assertThat(actualViolationMessages).contains(expectedViolationMessages);
    }
  }

  private static Stream<Arguments> provideSignupRequestScenarios() {
    return Stream.of(
      // valid
      Arguments.of(
        "Should accept valid request",
        "test@test.com",
        "John",
        "Doe",
        "password123",
        false,
        0,
        ""
      ),
      // Tests email
      Arguments.of(
        "Should reject invalid email format",
        "invalid-email",
        "John",
        "Doe",
        "password123",
        true,
        1, // @Email test
        "email: doit être une adresse électronique syntaxiquement correcte"
      ),
      Arguments.of(
        "Should reject email exceeding max length",
        "a".repeat(40) + "@" + "b".repeat(10) + ".com",
        "John",
        "Doe",
        "password123",
        true,
        1,
        "email: la taille doit être comprise entre 0 et 50"
      ),
      // Tests firstName
      Arguments.of(
        "Should reject firstName too short",
        "test@test.com",
        "Jo",
        "Doe",
        "password123",
        true,
        1,
        "firstName: la taille doit être comprise entre 3 et 20"
        //TO DO Confirm length choice ? "El Lissitzki" is a valid 2 letters Firstname
      ),
      Arguments.of(
        "Should reject firstName too long",
        "test@test.com",
        "J".repeat(21),
        "Doe",
        "password123",
        true,
        1,
        "firstName: la taille doit être comprise entre 3 et 20"
      ),
      // Tests lastName
      Arguments.of(
        "Should reject lastName too short",
        "test@test.com",
        "John",
        "Do",
        "password123",
        true,
        1,
        "lastName: la taille doit être comprise entre 3 et 20"
        //TO DO Confirm length choice ? "Cédric O" is a valid 1 letter surname
      ),
      Arguments.of(
        "Should reject lastName too long",
        "test@test.com",
        "John",
        "D".repeat(21),
        "password123",
        true,
        1,
        "lastName: la taille doit être comprise entre 3 et 20"
      ),
      // Tests password
      Arguments.of(
        "Should reject password too short",
        "test@test.com",
        "John",
        "Doe",
        "pass",
        true,
        1,
        "password: la taille doit être comprise entre 6 et 40"
      ),
      Arguments.of(
        "Should reject password too long",
        "test@test.com",
        "John",
        "Doe",
        "p".repeat(41),
        true,
        1,
        "password: la taille doit être comprise entre 6 et 40"
      ),
      // Tests null /empty /blank
      Arguments.of(
        "Should reject all null values",
        null,
        null,
        null,
        null,
        true,
        4, // 4 null violations (email,FirstName,LastName,password)
        "ne doit pas être vide"
      ),
      Arguments.of(
        "Should reject all empty values",
        "",
        "",
        "",
        "",
        true,
        7, // 4 empty violations (email,FirstName,LastName,password)
        //  +3 short violations (FirstName,LastName,password)
        "ne doit pas être vide"
      ),
      Arguments.of(
        "Should reject all blank values",
        "      ",
        "      ",
        "      ",
        "      ",
        true,
        5, // 4 empty violations (email,FirstName,LastName,password)
        //  +1 syntax violations (email)
        "ne doit pas être vide"
      ),
      // Test multiple violations
      Arguments.of(
        "Should detect multiple constraint violations",
        "invalid-email",
        "Jo",
        "Do",
        "12345",
        true,
        4, // 3 short violations (FirstName,LastName,password)
        //  +1 syntax violations (email)
        "la taille doit être comprise entre"
      )
    );
  }
}
