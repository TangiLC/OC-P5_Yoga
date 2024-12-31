package com.openclassrooms.starterjwt.security.services;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

  @ParameterizedTest
  @DisplayName("Should verify all account status methods return true")
  @CsvSource(
    {
      "isAccountNonExpired",
      "isAccountNonLocked",
      "isCredentialsNonExpired",
      "isEnabled",
    }
  )
  void testAccountStatusMethods(String methodName) throws Exception {
    boolean result = (boolean) UserDetailsImpl.class.getMethod(methodName)
      .invoke(userDetails);
    assertThat(result).isTrue();
  }

  @ParameterizedTest
  @CsvSource(
    {
      "2, user@test.com, Jane, Smith, false, pwd123",
      "3, admin@test.com, Bob, Brown, true, pwd456",
    }
  )
  @DisplayName("Should create various user details correctly")
  void testMultipleUserDetails(
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
}
