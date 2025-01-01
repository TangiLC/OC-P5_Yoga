package com.openclassrooms.starterjwt.payload.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lombok.var;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SignupRequestUnitTest {

  private static final Validator validator;

  static {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @DisplayName("Should handle validate SignupRequest scenarios")
  @ParameterizedTest(name = "{0}")
  @CsvSource(
    {
      "Valid request, test@test.com, John, Doe, password123, false, 0, ''",
      "Invalid email format, invalid-email, John, Doe, password123, true, 1, 'email: doit être une adresse électronique syntaxiquement correcte'",
      "Email exceeding max length, aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@bbbbbbbbbb.com, John, Doe, password123, true, 1, 'email: la taille doit être comprise entre 0 et 50'",
      "FirstName too short, test@test.com, Jo, Doe, password123, true, 1, 'firstName: la taille doit être comprise entre 3 et 20'",
      "FirstName too long, test@test.com, JJJJJJJJJJJJJJJJJJJJJ, Doe, password123, true, 1, 'firstName: la taille doit être comprise entre 3 et 20'",
      "LastName too short, test@test.com, John, Do, password123, true, 1, 'lastName: la taille doit être comprise entre 3 et 20'",
      "LastName too long, test@test.com, John, DDDDDDDDDDDDDDDDDDDDD, password123, true, 1, 'lastName: la taille doit être comprise entre 3 et 20'",
      "Password too short, test@test.com, John, Doe, pass, true, 1, 'password: la taille doit être comprise entre 6 et 40'",
      "Password too long, test@test.com, John, Doe, ppppppppppppppppppppppppppppppppppppppppppp, true, 1, 'password: la taille doit être comprise entre 6 et 40'",
      "All null values, , , , , true, 4, 'ne doit pas être vide'",
      "All empty values, '', '', '', '', true, 7, 'ne doit pas être vide'",
      "All blank values, '      ', '      ', '      ', '      ', true, 5, 'ne doit pas être vide'",
      "Multiple constraint violations, invalid-email, Jo, Do, 12345, true, 4, 'la taille doit être comprise entre'",
    }
  )
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
}
