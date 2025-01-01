package com.openclassrooms.starterjwt.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.openclassrooms.starterjwt.models.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {

  @Autowired
  private UserRepository userRepository;

  @ParameterizedTest
  @CsvSource(
    {
      "yoga@studio.com, John, Doe, true, true", // Existing admin user
      "user@studio.com, Jane, Smith, false, true", // Existing regular user
      "unknown@studio.com, , , false, false", // Non-existing user
    }
  )
  @DisplayName("Should find user by email in database")
  void testFindByEmail(
    String email,
    String firstName,
    String lastName,
    boolean isAdmin,
    boolean shouldExist
  ) {
    // Prepare test data
    if (shouldExist) {
      User user = new User();
      user
        .setEmail(email)
        .setFirstName(firstName)
        .setLastName(lastName)
        .setPassword("encodedPassword")
        .setAdmin(isAdmin);
      userRepository.save(user);
    }

    // Execute
    Optional<User> result = userRepository.findByEmail(email);

    // Verify
    if (shouldExist) {
      assertThat(result).isPresent();
      assertThat(result.get().getEmail()).isEqualTo(email);
      assertThat(result.get().getFirstName()).isEqualTo(firstName);
      assertThat(result.get().getLastName()).isEqualTo(lastName);
      assertThat(result.get().isAdmin()).isEqualTo(isAdmin);
    } else {
      assertThat(result).isEmpty();
    }
  }

  @ParameterizedTest
  @CsvSource(
    {
      "yoga@studio.com, true", // Email exists
      "unknown@studio.com, false", // Email doesn't exist
    }
  )
  @DisplayName("Should check if email exists in database")
  void testExistsByEmail(String email, boolean shouldExist) {
    // Prepare test data
    if (shouldExist) {
      User user = new User();
      user
        .setEmail(email)
        .setFirstName("John")
        .setLastName("Doe")
        .setPassword("encodedPassword")
        .setAdmin(true);
      userRepository.save(user);
    }

    // Execute
    boolean exists = userRepository.existsByEmail(email);

    // Verify
    assertThat(exists).isEqualTo(shouldExist);
  }

  @ParameterizedTest
  @CsvSource(
    {
      "new@studio.com, John, Doe, true", // New admin user
      "other@studio.com, Jane, Smith, false", // New regular user
    }
  )
  @DisplayName("Should save new user in database")
  @Transactional
  void testSaveUser(
    String email,
    String firstName,
    String lastName,
    boolean isAdmin
  ) {
    // Prepare
    User user = new User();
    user
      .setEmail(email)
      .setFirstName(firstName)
      .setLastName(lastName)
      .setPassword("encodedPassword")
      .setAdmin(isAdmin);

    // Execute
    User savedUser = userRepository.save(user);

    // Verify
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getEmail()).isEqualTo(email);
    assertThat(savedUser.getFirstName()).isEqualTo(firstName);
    assertThat(savedUser.getLastName()).isEqualTo(lastName);
    assertThat(savedUser.isAdmin()).isEqualTo(isAdmin);

    // Verify persistence
    assertThat(userRepository.findById(savedUser.getId())).isPresent();
  }
}
