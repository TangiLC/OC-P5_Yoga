package com.openclassrooms.starterjwt.security.services;

import static org.assertj.core.api.Assertions.*;

import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.security.core.GrantedAuthority;

class UserDetailsImplTest {

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

  @Test
  @DisplayName("Should create UserDetailsImpl with correct values")
  void testUserDetailsCreation() {
    assertThat(userDetails.getId()).isEqualTo(1L);
    assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
    assertThat(userDetails.getFirstName()).isEqualTo("John");
    assertThat(userDetails.getLastName()).isEqualTo("Doe");
    assertThat(userDetails.getAdmin()).isTrue();
    assertThat(userDetails.getPassword()).isEqualTo("securePassword");
  }

  @Test
  @DisplayName("Should return empty authorities")
  void testGetAuthorities() {
    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
    assertThat(authorities).isNotNull().isEmpty();
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

  @Test
  @DisplayName("Should verify equals method")
  void testEquals() {
    UserDetailsImpl sameDude = UserDetailsImpl.builder().id(1L).build();
    UserDetailsImpl otherDude = UserDetailsImpl.builder().id(2L).build();

    assertThat(userDetails)
      .isEqualTo(userDetails)
      .isEqualTo(sameDude)
      .isNotEqualTo(otherDude)
      .isNotEqualTo(null)
      .isNotEqualTo(new Object());
  }

  @Test
  @DisplayName("Should handle null values in builder")
  void testNullValues() {
    UserDetailsImpl nullUser = UserDetailsImpl.builder().build();

    assertThat(nullUser.getId()).isNull();
    assertThat(nullUser.getUsername()).isNull();
    assertThat(nullUser.getFirstName()).isNull();
    assertThat(nullUser.getLastName()).isNull();
    assertThat(nullUser.getAdmin()).isNull();
    assertThat(nullUser.getPassword()).isNull();
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
