package com.openclassrooms.starterjwt.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.models.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuiteDisplayName("REPOSITORY")
@DisplayName("Unit test for UserRepository")
class UserRepositoryUnitTest {

  @Mock
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @ParameterizedTest(name = "({index}) : {0} [{2}]")
  @CsvSource(
    {
      "Existing email, yoga@studio.com, true",
      "Unknown email, unknown@studio.com, false",
    }
  )
  @DisplayName("Should find user by email ")
  void testFindByEmail(String scenarioName, String email, boolean shouldExist) {
    User user = new User();
    user
      .setEmail("yoga@studio.com")
      .setFirstName("John")
      .setLastName("Doe")
      .setPassword("encodedPassword")
      .setAdmin(true);

    when(userRepository.findByEmail("yoga@studio.com"))
      .thenReturn(Optional.of(user));
    when(userRepository.findByEmail("unknown@studio.com"))
      .thenReturn(Optional.empty());

    Optional<User> result = userRepository.findByEmail(email);

    if (shouldExist) {
      assertThat(result).isPresent();
      assertThat(result.get().getEmail()).isEqualTo(email);
    } else {
      assertThat(result).isEmpty();
    }
    verify(userRepository, times(1)).findByEmail(email);
  }

  @ParameterizedTest(name = "({index}) : {0} [{2}]")
  @CsvSource(
    {
      "Existing email, yoga@studio.com, true",
      "Unknown email, unknown@studio.com, false",
    }
  )
  @DisplayName("Should check if email exists ")
  void testExistsByEmail(String email, boolean shouldExist) {
    when(userRepository.existsByEmail("yoga@studio.com")).thenReturn(true);
    when(userRepository.existsByEmail("unknown@studio.com")).thenReturn(false);

    boolean exists = userRepository.existsByEmail(email);

    assertThat(exists).isEqualTo(shouldExist);
    verify(userRepository, times(1)).existsByEmail(email);
  }
}
