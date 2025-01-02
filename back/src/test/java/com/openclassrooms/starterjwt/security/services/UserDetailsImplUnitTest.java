package com.openclassrooms.starterjwt.security.services;

import static org.assertj.core.api.Assertions.*;

import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SuiteDisplayName("SECURITY")
@DisplayName("Unit tests for UserDetailsImpl")
class UserDetailsImplUnitTest {

  private UserDetailsImpl userDetails;

  @BeforeEach
  void setUp() {
    userDetails =
      UserDetailsImpl
        .builder()
        .id(1L)
        .username("test@example.com")
        .firstName("John")
        .lastName("Doe")
        .admin(true)
        .password("securePassword")
        .build();
  }

  @ParameterizedTest(name = "({index}) : {0} [{2}]")
  @DisplayName("Should verify all account status methods")
  @CsvSource(
    {
      "Not expired, isAccountNonExpired, true",
      "Not Locked, isAccountNonLocked, true",
      "Credentials not expired, isCredentialsNonExpired, true",
      "Enabled, isEnabled, true",
    }
  )
  void testAccountStatusMethods(
    String scenarioName,
    String methodName,
    String expectedResult
  ) throws Exception {
    boolean result = (boolean) UserDetailsImpl.class.getMethod(methodName)
      .invoke(userDetails);
    assertThat(result).isTrue();
  }

  @ParameterizedTest(name="({index}) : {0}")
  @CsvSource(
    {
      "Create user account, 2, user@test.com, Jane, Smith, false, pwd123",
      "Create admin account, 3, admin@test.com, Bob, Brown, true, pwd456",
    }
  )
  @DisplayName("Should create various user details correctly ")
  void testMultipleUserDetails(
    String scenarioName,
    Long id,
    String username,
    String firstName,
    String lastName,
    boolean admin,
    String password
  ) {
    UserDetailsImpl testUser = UserDetailsImpl
      .builder()
      .id(id)
      .username(username)
      .firstName(firstName)
      .lastName(lastName)
      .admin(admin)
      .password(password)
      .build();

    assertThat(testUser.getId()).isEqualTo(id);
    assertThat(testUser.getUsername()).isEqualTo(username);
    assertThat(testUser.getFirstName()).isEqualTo(firstName);
    assertThat(testUser.getLastName()).isEqualTo(lastName);
    assertThat(testUser.getAdmin()).isEqualTo(admin);
    assertThat(testUser.getPassword()).isEqualTo(password);
  }

  @ParameterizedTest(name="({index}) : {0} [{7}]")
  @CsvSource(
    {
      "'exactly same', 1, test@example.com, John, Doe, true, securePassword, true",
      "'different class', 0, , , , , java.lang.String, false",
      "'same ID', 1, test2@example.com, Jane, Smith, false, anotherPassword, true",
      "'different ID', 2, test3@example.com, Jack, Black, false, diffPassword, false",
    }
  )
  @DisplayName("Should verify equals method works correctly ")
  void testEqualsParameterized(
    String scenarioName,
    Long id,
    String username,
    String firstName,
    String lastName,
    Boolean admin,
    String password,
    boolean expectedResult
  ) {
    Object other = "java.lang.String".equals(password)
      ? "A String"
      : id == null
        ? null
        : UserDetailsImpl
          .builder()
          .id(id)
          .username(username)
          .firstName(firstName)
          .lastName(lastName)
          .admin(admin)
          .password(password)
          .build();

    boolean result = userDetails.equals(other);

    assertThat(result).as("Scenario: %s", scenarioName).isEqualTo(expectedResult);
  }
}
