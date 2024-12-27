package com.openclassrooms.starterjwt.payload.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LombokSignupRequestUnitTest {

  private static final String TEST_EMAIL = "test@test.com";
  private static final String TEST_FIRSTNAME = "John";
  private static final String TEST_LASTNAME = "Doe";
  private static final String TEST_PASSWORD = "password123";

  @Test
  @DisplayName("Should test equals with partially initialized objects")
  void should_test_equals_with_partial_initialization() {
    // Given
    SignupRequest fullRequest = new SignupRequest();
    fullRequest.setEmail(TEST_EMAIL);
    fullRequest.setFirstName(TEST_FIRSTNAME);
    fullRequest.setLastName(TEST_LASTNAME);
    fullRequest.setPassword(TEST_PASSWORD);

    SignupRequest partialRequest1 = new SignupRequest();
    partialRequest1.setEmail(TEST_EMAIL);
    partialRequest1.setFirstName(TEST_FIRSTNAME);
    // lastName and password are null

    SignupRequest partialRequest2 = new SignupRequest();
    partialRequest2.setLastName(TEST_LASTNAME);
    partialRequest2.setPassword(TEST_PASSWORD);
    // email and firstName are null

    SignupRequest partialRequest3 = new SignupRequest();
    partialRequest3.setEmail(TEST_EMAIL);
    partialRequest3.setPassword(TEST_PASSWORD);
    // firstName and lastName are null

    // Then
    assertThat(fullRequest.equals(partialRequest1)).isFalse();
    assertThat(fullRequest.equals(partialRequest2)).isFalse();
    assertThat(fullRequest.equals(partialRequest3)).isFalse();
    assertThat(partialRequest1.equals(partialRequest2)).isFalse();
    assertThat(partialRequest1.equals(partialRequest3)).isFalse();
    assertThat(partialRequest3.equals(partialRequest2)).isFalse();
  }

  @Test
  @DisplayName("Should test equals, hashCode and toString methods")
  void should_test_equals_hashcode_toString() {
    // Given
    SignupRequest request1 = new SignupRequest();
    request1.setEmail(TEST_EMAIL);
    request1.setFirstName(TEST_FIRSTNAME);
    request1.setLastName(TEST_LASTNAME);
    request1.setPassword(TEST_PASSWORD);

    SignupRequest request2 = new SignupRequest();
    request2.setEmail(TEST_EMAIL);
    request2.setFirstName(TEST_FIRSTNAME);
    request2.setLastName(TEST_LASTNAME);
    request2.setPassword(TEST_PASSWORD);

    SignupRequest differentRequest = new SignupRequest();
    differentRequest.setEmail("different@test.com");
    differentRequest.setFirstName(TEST_FIRSTNAME);
    differentRequest.setLastName(TEST_LASTNAME);
    differentRequest.setPassword(TEST_PASSWORD);

    assertThat(request1)
      .isEqualTo(request1)
      .isEqualTo(request2)
      .hasSameHashCodeAs(request2)
      .isNotEqualTo(null)
      .isNotEqualTo(new Object())
      .isNotEqualTo(differentRequest)
      .hasSameHashCodeAs(request1);

    assertThat(request2).isEqualTo(request1);

    // Test toString
    assertThat(request1.toString())
      .contains(TEST_EMAIL)
      .contains(TEST_FIRSTNAME)
      .contains(TEST_LASTNAME)
      .contains("password");
  }

  @DisplayName("Should test equals with different field values")
  @ParameterizedTest(name = "[{index}] Testing equality with {0}")
  @MethodSource("provideFieldVariations")
  void should_test_equals_with_different_fields(
    String testName,
    String email,
    String firstName,
    String lastName,
    String password,
    boolean expectedEqual
  ) {
    // Given
    SignupRequest baseRequest = new SignupRequest();
    baseRequest.setEmail(TEST_EMAIL);
    baseRequest.setFirstName(TEST_FIRSTNAME);
    baseRequest.setLastName(TEST_LASTNAME);
    baseRequest.setPassword(TEST_PASSWORD);

    SignupRequest comparedRequest = new SignupRequest();
    comparedRequest.setEmail(email);
    comparedRequest.setFirstName(firstName);
    comparedRequest.setLastName(lastName);
    comparedRequest.setPassword(password);

    // Then
    assertThat(baseRequest.equals(comparedRequest)).isEqualTo(expectedEqual);
    if (expectedEqual) {
      assertThat(baseRequest.hashCode()).isEqualTo(comparedRequest.hashCode());
    }
  }

  private static Stream<Arguments> provideFieldVariations() {
    return Stream.of(
      Arguments.of(
        "identical objects",
        TEST_EMAIL,
        TEST_FIRSTNAME,
        TEST_LASTNAME,
        TEST_PASSWORD,
        true
      ),
      Arguments.of(
        "different email",
        "different@test.com",
        TEST_FIRSTNAME,
        TEST_LASTNAME,
        TEST_PASSWORD,
        false
      ),
      Arguments.of(
        "different firstName",
        TEST_EMAIL,
        "Jane",
        TEST_LASTNAME,
        TEST_PASSWORD,
        false
      ),
      Arguments.of(
        "different lastName",
        TEST_EMAIL,
        TEST_FIRSTNAME,
        "Smith",
        TEST_PASSWORD,
        false
      ),
      Arguments.of(
        "different password",
        TEST_EMAIL,
        TEST_FIRSTNAME,
        TEST_LASTNAME,
        "different_password",
        false
      ),
      Arguments.of("all fields null", null, null, null, null, false)
    );
  }
}
