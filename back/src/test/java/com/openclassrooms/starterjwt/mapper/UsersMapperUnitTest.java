package com.openclassrooms.starterjwt.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

class UsersMapperTest {

  private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
  private static final LocalDateTime FIXED_DATE = LocalDateTime.of(
    2024,
    1,
    1,
    0,
    0
  );

  private static List<User> createUserList() {
    return List.of(
      User
        .builder()
        .id(1L)
        .email("user1@test.com")
        .firstName("John")
        .lastName("Doe")
        .password("password123")
        .admin(false)
        .createdAt(FIXED_DATE)
        .updatedAt(FIXED_DATE)
        .build(),
      User
        .builder()
        .id(2L)
        .email("admin@test.com")
        .firstName("Admin")
        .lastName("User")
        .password("admin123")
        .admin(true)
        .createdAt(FIXED_DATE)
        .updatedAt(FIXED_DATE)
        .build()
    );
  }

  private static List<UserDto> createUserDtoList() {
    UserDto userDto1 = new UserDto();
    userDto1.setId(1L);
    userDto1.setEmail("user1@test.com");
    userDto1.setFirstName("John");
    userDto1.setLastName("Doe");
    userDto1.setPassword("password123");
    userDto1.setAdmin(false);
    userDto1.setCreatedAt(FIXED_DATE);
    userDto1.setUpdatedAt(FIXED_DATE);

    UserDto userDto2 = new UserDto();
    userDto2.setId(2L);
    userDto2.setEmail("admin@test.com");
    userDto2.setFirstName("Admin");
    userDto2.setLastName("User");
    userDto2.setPassword("admin123");
    userDto2.setAdmin(true);
    userDto2.setCreatedAt(FIXED_DATE);
    userDto2.setUpdatedAt(FIXED_DATE);

    return List.of(userDto1, userDto2);
  }

  private static Stream<Arguments> toDtoListTestCases() {
    return Stream.of(Arguments.of(createUserList(), createUserDtoList()));
  }

  private static Stream<Arguments> toEntityListTestCases() {
    return Stream.of(Arguments.of(createUserDtoList(), createUserList()));
  }

  @ParameterizedTest
  @MethodSource("toDtoListTestCases")
  void toDtoList_ShouldMapCorrectly(
    List<User> users,
    List<UserDto> expectedUserDtos
  ) {
    List<UserDto> result = userMapper.toDto(users);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(expectedUserDtos.size());

    for (int i = 0; i < expectedUserDtos.size(); i++) {
      UserDto expected = expectedUserDtos.get(i);
      UserDto actual = result.get(i);

      assertThat(actual.getId()).isEqualTo(expected.getId());
      assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
      assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
      assertThat(actual.getLastName()).isEqualTo(expected.getLastName());
      assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
      assertThat(actual.isAdmin()).isEqualTo(expected.isAdmin());
      assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
      assertThat(actual.getUpdatedAt()).isEqualTo(expected.getUpdatedAt());
    }
  }

  @ParameterizedTest
  @MethodSource("toEntityListTestCases")
  void toEntityList_ShouldMapCorrectly(
    List<UserDto> userDtos,
    List<User> expectedUsers
  ) {
    List<User> result = userMapper.toEntity(userDtos);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(expectedUsers.size());

    for (int i = 0; i < expectedUsers.size(); i++) {
      User expected = expectedUsers.get(i);
      User actual = result.get(i);

      assertThat(actual.getId()).isEqualTo(expected.getId());
      assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
      assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
      assertThat(actual.getLastName()).isEqualTo(expected.getLastName());
      assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
      assertThat(actual.isAdmin()).isEqualTo(expected.isAdmin());
      assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
      assertThat(actual.getUpdatedAt()).isEqualTo(expected.getUpdatedAt());
    }
  }
}
