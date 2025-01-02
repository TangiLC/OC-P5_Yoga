package com.openclassrooms.starterjwt.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.openclassrooms.starterjwt.models.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test")
@SuiteDisplayName("REPOSITORY")
@DisplayName("¤Integration tests for UserRepository")
class UserRepositoryIntegrationTest {

  @Autowired
  private UserRepository userRepository;

  @ParameterizedTest(name = "({index}) : {0} [{5}]")
  @CsvSource(
    {
      "Existing admin user, yoga@studio.com, John, Doe, true, true",
      "Existing regular user, user@studio.com, Jane, Smith, false, true",
      "Unknown user, unknown@studio.com, , , false, false",
    }
  )
  @DisplayName("Should find user by email in database ")
  void testFindByEmail(
    String scenarioName,
    String email,
    String firstName,
    String lastName,
    boolean isAdmin,
    boolean shouldExist
  ) {
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

    Optional<User> result = userRepository.findByEmail(email);

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

  @ParameterizedTest(name = "({index}) : {0} [{2}]")
  @CsvSource(
    {
      "Existing email, yoga@studio.com, true",
      "Unknown email, unknown@studio.com, false",
    }
  )
  @DisplayName("Should check if email exists in database ")
  void testExistsByEmail(String email, boolean shouldExist) {
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

    boolean exists = userRepository.existsByEmail(email);

    assertThat(exists).isEqualTo(shouldExist);
  }

  @ParameterizedTest(name = "({index}) : {0} [{4}]")
  @CsvSource(
    {
      "New admin user, new@studio.com, John, Doe, true",
      "New regular user, other@studio.com, Jane, Smith, false",
    }
  )
  @DisplayName("Should save new user in database ")
  @Transactional
  void testSaveUser(
    String scenarioName,
    String email,
    String firstName,
    String lastName,
    boolean isAdmin
  ) {
    User user = new User();
    user
      .setEmail(email)
      .setFirstName(firstName)
      .setLastName(lastName)
      .setPassword("encodedPassword")
      .setAdmin(isAdmin);

    User savedUser = userRepository.save(user);

    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getEmail()).isEqualTo(email);
    assertThat(savedUser.getFirstName()).isEqualTo(firstName);
    assertThat(savedUser.getLastName()).isEqualTo(lastName);
    assertThat(savedUser.isAdmin()).isEqualTo(isAdmin);

    assertThat(userRepository.findById(savedUser.getId())).isPresent();
  }
}
