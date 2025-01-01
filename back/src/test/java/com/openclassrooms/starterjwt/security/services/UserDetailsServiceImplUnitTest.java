package com.openclassrooms.starterjwt.security.services;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class UserDetailsServiceImplUnitTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserDetailsServiceImpl userDetailsService;

  private User mockUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockUser = new User();
  }

  @ParameterizedTest
  @CsvSource(
    {
      "test@example.com, John, Doe, password123",
      "admin@test.com, Jane, Smith, securePass",
      "user@domain.com, Bob, Brown, userPass",
    }
  )
  @DisplayName("Should return UserDetails when user is found")
  void testLoadUserByUsername_UserFound(
    String email,
    String firstName,
    String lastName,
    String password
  ) {
    mockUser.setId(1L);
    mockUser.setEmail(email);
    mockUser.setFirstName(firstName);
    mockUser.setLastName(lastName);
    mockUser.setPassword(password);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

    assertThat(userDetails).isNotNull().isInstanceOf(UserDetailsImpl.class);

    UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;

    assertThat(userDetailsImpl.getUsername()).isEqualTo(email);
    assertThat(userDetailsImpl.getPassword()).isEqualTo(password);
    assertThat(userDetailsImpl.getFirstName()).isEqualTo(firstName);
    assertThat(userDetailsImpl.getLastName()).isEqualTo(lastName);
    assertThat(userDetailsImpl.getId()).isEqualTo(1L);

    verify(userRepository).findByEmail(email);
  }

  @ParameterizedTest
  @CsvSource(
    { "nobody@example.com", "invalid@test.com", "nonexistent@domain.com" }
  )
  @DisplayName(
    "Should throw UsernameNotFoundException when user is not found"
  )
  void testLoadUserByUsername_UserNotFound(String email) {
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
      .isInstanceOf(UsernameNotFoundException.class)
      .hasMessage("User Not Found with email: " + email);

    verify(userRepository).findByEmail(email);
  }

  @Test
  @DisplayName("Should handle null values in user object")
  void testLoadUserByUsername_NullValues() {
    String email = "test@example.com";
    mockUser.setEmail(email);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

    assertThat(userDetails).isNotNull().isInstanceOf(UserDetailsImpl.class);

    UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;

    assertThat(userDetailsImpl.getUsername()).isEqualTo(email);
    assertThat(userDetailsImpl.getFirstName()).isNull();
    assertThat(userDetailsImpl.getLastName()).isNull();
    assertThat(userDetailsImpl.getPassword()).isNull();
    assertThat(userDetailsImpl.getId()).isNull();

    verify(userRepository).findByEmail(email);
  }
}
