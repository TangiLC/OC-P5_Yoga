package com.openclassrooms.starterjwt.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.models.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserRepositoryUnitTest {

  @Mock
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @ParameterizedTest
  @CsvSource(
    {
      "yoga@studio.com, true", // Existing email
      "unknown@studio.com, false", // Non-existing email
    }
  )
  @DisplayName("Should find user by email")
  void testFindByEmail(String email, boolean shouldExist) {
    // Prepare
    User user = new User();
    user
      .setEmail("yoga@studio.com")
      .setFirstName("John")
      .setLastName("Doe")
      .setPassword("encodedPassword")
      .setAdmin(true);

    // Mock repository behavior
    when(userRepository.findByEmail("yoga@studio.com"))
      .thenReturn(Optional.of(user));
    when(userRepository.findByEmail("unknown@studio.com"))
      .thenReturn(Optional.empty());

    // Execute
    Optional<User> result = userRepository.findByEmail(email);

    // Verify
    if (shouldExist) {
      assertThat(result).isPresent();
      assertThat(result.get().getEmail()).isEqualTo(email);
    } else {
      assertThat(result).isEmpty();
    }
    verify(userRepository, times(1)).findByEmail(email);
  }

  @ParameterizedTest
  @CsvSource(
    {
      "yoga@studio.com, true", // Existing email
      "unknown@studio.com, false", // Non-existing email
    }
  )
  @DisplayName("Should check if email exists")
  void testExistsByEmail(String email, boolean shouldExist) {
    // Mock repository behavior
    when(userRepository.existsByEmail("yoga@studio.com")).thenReturn(true);
    when(userRepository.existsByEmail("unknown@studio.com")).thenReturn(false);

    // Execute
    boolean exists = userRepository.existsByEmail(email);

    // Verify
    assertThat(exists).isEqualTo(shouldExist);
    verify(userRepository, times(1)).existsByEmail(email);
  }
}
