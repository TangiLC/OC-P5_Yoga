package com.openclassrooms.starterjwt.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

class UserMapperTest {

  private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
  private static final LocalDateTime FIXED_DATE = LocalDateTime.of(
    2024,
    1,
    1,
    0,
    0
  );

  private static User createUser(
    Long id,
    String email,
    String firstName,
    String lastName,
    String password,
    boolean admin,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
  ) {
    return User
      .builder()
      .id(id)
      .email(email)
      .firstName(firstName)
      .lastName(lastName)
      .password(password)
      .admin(admin)
      .createdAt(createdAt)
      .updatedAt(updatedAt)
      .build();
  }

  private static UserDto createUserDto(
    Long id,
    String email,
    String firstName,
    String lastName,
    String password,
    boolean admin,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
  ) {
    UserDto userDto = new UserDto();
    userDto.setId(id);
    userDto.setEmail(email);
    userDto.setFirstName(firstName);
    userDto.setLastName(lastName);
    userDto.setPassword(password);
    userDto.setAdmin(admin);
    userDto.setCreatedAt(createdAt);
    userDto.setUpdatedAt(updatedAt);
    return userDto;
  }

  private static Stream<Arguments> toDtoTestCases() {
    return Stream.of(
      // Cas nominal
      Arguments.of(
        createUser(
          1L,
          "user@test.com",
          "John",
          "Doe",
          "password123",
          false,
          FIXED_DATE,
          FIXED_DATE
        ),
        1L,
        "user@test.com",
        "John",
        "Doe",
        "password123",
        false,
        FIXED_DATE,
        FIXED_DATE
      ),
      // Admin user
      Arguments.of(
        createUser(
          2L,
          "admin@test.com",
          "Admin",
          "User",
          "admin123",
          true,
          FIXED_DATE,
          FIXED_DATE
        ),
        2L,
        "admin@test.com",
        "Admin",
        "User",
        "admin123",
        true,
        FIXED_DATE,
        FIXED_DATE
      ),
      // Sans ID
      Arguments.of(
        createUser(
          null,
          "new@test.com",
          "New",
          "User",
          "newpass",
          false,
          null,
          null
        ),
        null,
        "new@test.com",
        "New",
        "User",
        "newpass",
        false,
        null,
        null
      ),
      // Avec caractères spéciaux
      Arguments.of(
        createUser(
          3L,
          "user.name@test.com",
          "Jöhn",
          "D'öe",
          "pass123",
          false,
          FIXED_DATE,
          FIXED_DATE
        ),
        3L,
        "user.name@test.com",
        "Jöhn",
        "D'öe",
        "pass123",
        false,
        FIXED_DATE,
        FIXED_DATE
      )
    );
  }

  private static Stream<Arguments> toEntityTestCases() {
    return Stream.of(
      // Cas nominal
      Arguments.of(
        createUserDto(
          1L,
          "user@test.com",
          "John",
          "Doe",
          "password123",
          false,
          FIXED_DATE,
          FIXED_DATE
        ),
        1L,
        "user@test.com",
        "John",
        "Doe",
        "password123",
        false,
        FIXED_DATE,
        FIXED_DATE
      ),
      // Admin user
      Arguments.of(
        createUserDto(
          2L,
          "admin@test.com",
          "Admin",
          "User",
          "admin123",
          true,
          FIXED_DATE,
          FIXED_DATE
        ),
        2L,
        "admin@test.com",
        "Admin",
        "User",
        "admin123",
        true,
        FIXED_DATE,
        FIXED_DATE
      ),
      // Sans ID
      Arguments.of(
        createUserDto(
          null,
          "new@test.com",
          "New",
          "User",
          "newpass",
          false,
          null,
          null
        ),
        null,
        "new@test.com",
        "New",
        "User",
        "newpass",
        false,
        null,
        null
      ),
      // Avec caractères spéciaux
      Arguments.of(
        createUserDto(
          3L,
          "user.name@test.com",
          "Jöhn",
          "D'öe",
          "pass123",
          false,
          FIXED_DATE,
          FIXED_DATE
        ),
        3L,
        "user.name@test.com",
        "Jöhn",
        "D'öe",
        "pass123",
        false,
        FIXED_DATE,
        FIXED_DATE
      )
    );
  }

  @ParameterizedTest
  @MethodSource("toDtoTestCases")
  void toDto_ShouldMapCorrectly(
    User user,
    Long expectedId,
    String expectedEmail,
    String expectedFirstName,
    String expectedLastName,
    String expectedPassword,
    boolean expectedAdmin,
    LocalDateTime expectedCreatedAt,
    LocalDateTime expectedUpdatedAt
  ) {
    UserDto result = userMapper.toDto(user);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(expectedId);
    assertThat(result.getEmail()).isEqualTo(expectedEmail);
    assertThat(result.getFirstName()).isEqualTo(expectedFirstName);
    assertThat(result.getLastName()).isEqualTo(expectedLastName);
    assertThat(result.getPassword()).isEqualTo(expectedPassword);
    assertThat(result.isAdmin()).isEqualTo(expectedAdmin);
    assertThat(result.getCreatedAt()).isEqualTo(expectedCreatedAt);
    assertThat(result.getUpdatedAt()).isEqualTo(expectedUpdatedAt);
  }

  @ParameterizedTest
  @MethodSource("toEntityTestCases")
  void toEntity_ShouldMapCorrectly(
    UserDto userDto,
    Long expectedId,
    String expectedEmail,
    String expectedFirstName,
    String expectedLastName,
    String expectedPassword,
    boolean expectedAdmin,
    LocalDateTime expectedCreatedAt,
    LocalDateTime expectedUpdatedAt
  ) {
    User result = userMapper.toEntity(userDto);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(expectedId);
    assertThat(result.getEmail()).isEqualTo(expectedEmail);
    assertThat(result.getFirstName()).isEqualTo(expectedFirstName);
    assertThat(result.getLastName()).isEqualTo(expectedLastName);
    assertThat(result.getPassword()).isEqualTo(expectedPassword);
    assertThat(result.isAdmin()).isEqualTo(expectedAdmin);
    assertThat(result.getCreatedAt()).isEqualTo(expectedCreatedAt);
    assertThat(result.getUpdatedAt()).isEqualTo(expectedUpdatedAt);
  }
}
