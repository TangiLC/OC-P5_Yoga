package com.openclassrooms.starterjwt.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionMapperTest {

  @Mock
  private TeacherService teacherService;

  @Mock
  private UserService userService;

  @InjectMocks
  private SessionMapperImpl sessionMapper;

  private static final Date TEST_DATE = new Date();
  private static final LocalDateTime TEST_DATETIME = LocalDateTime.now();

  private static User createUser(Long id) {
    User user = new User();
    user.setId(id);
    return user;
  }

  private static Stream<Arguments> toEntityTestCases() {
    return Stream.of(
      Arguments.of(
        new SessionDto(
          1L,
          "Session 1",
          TEST_DATE,
          1L,
          "Description 1",
          Arrays.asList(11L, 12L),
          TEST_DATETIME,
          TEST_DATETIME
        ),
        true,
        2
      ),
      Arguments.of(
        new SessionDto(
          2L,
          "Session 2",
          TEST_DATE,
          null,
          "Description 2",
          Arrays.asList(11L),
          TEST_DATETIME,
          TEST_DATETIME
        ),
        false,
        1
      ),
      Arguments.of(
        new SessionDto(
          3L,
          "Session 3",
          TEST_DATE,
          1L,
          "Description 3",
          null,
          TEST_DATETIME,
          TEST_DATETIME
        ),
        true,
        0
      ),
      Arguments.of(
        new SessionDto(
          4L,
          "Session 4",
          TEST_DATE,
          null,
          "Description 4",
          Collections.emptyList(),
          TEST_DATETIME,
          TEST_DATETIME
        ),
        false,
        0
      )
    );
  }

  private static Stream<Arguments> toDtoTestCases() {
    return Stream.of(
      Arguments.of(
        createFullSession(
          1L,
          "Session 1",
          TEST_DATE,
          "Description 1",
          1L,
          Arrays.asList(1L, 2L),
          TEST_DATETIME,
          TEST_DATETIME
        ),
        true,
        2
      ),
      Arguments.of(
        createSessionWithoutTeacher(
          2L,
          "Session 2",
          TEST_DATE,
          "Description 2",
          Arrays.asList(1L),
          TEST_DATETIME,
          TEST_DATETIME
        ),
        false,
        1
      ),
      Arguments.of(
        createSessionWithoutUsers(
          3L,
          "Session 3",
          TEST_DATE,
          "Description 3",
          1L,
          TEST_DATETIME,
          TEST_DATETIME
        ),
        true,
        0
      )
    );
  }

  @ParameterizedTest
  @MethodSource("toEntityTestCases")
  void toEntity_ShouldMapCorrectly(
    SessionDto dto,
    boolean hasTeacher,
    int expectedUsers
  ) {
    if (dto.getTeacher_id() != null) {
      Teacher teacher = new Teacher();
      teacher.setId(dto.getTeacher_id());
      when(teacherService.findById(dto.getTeacher_id())).thenReturn(teacher);
    }

    if (dto.getUsers() != null) {
      for (Long userId : dto.getUsers()) {
        when(userService.findById(userId)).thenReturn(createUser(userId));
      }
    }

    Session result = sessionMapper.toEntity(dto);

    // Assertions
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(dto.getName());
    assertThat(result.getDate()).isEqualTo(dto.getDate());
    assertThat(result.getDescription()).isEqualTo(dto.getDescription());
    if (hasTeacher) {
      assertThat(result.getTeacher()).isNotNull();
      assertThat(result.getTeacher().getId()).isEqualTo(dto.getTeacher_id());
    } else {
      assertThat(result.getTeacher()).isNull();
    }
    assertThat(result.getUsers()).hasSize(expectedUsers);
    assertThat(result.getCreatedAt()).isEqualTo(dto.getCreatedAt());
    assertThat(result.getUpdatedAt()).isEqualTo(dto.getUpdatedAt());
  }

  @ParameterizedTest
  @MethodSource("toDtoTestCases")
  void toDto_ShouldMapCorrectly(
    Session session,
    boolean hasTeacher,
    int expectedUsers
  ) {
    SessionDto result = sessionMapper.toDto(session);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(session.getName());
    assertThat(result.getDate()).isEqualTo(session.getDate());
    assertThat(result.getDescription()).isEqualTo(session.getDescription());
    if (hasTeacher) {
      assertThat(result.getTeacher_id())
        .isEqualTo(session.getTeacher().getId());
    } else {
      assertThat(result.getTeacher_id()).isNull();
    }
    assertThat(result.getUsers()).hasSize(expectedUsers);
    assertThat(result.getCreatedAt()).isEqualTo(session.getCreatedAt());
    assertThat(result.getUpdatedAt()).isEqualTo(session.getUpdatedAt());
  }

  private static Session createFullSession(
    Long id,
    String name,
    Date date,
    String description,
    Long teacherId,
    List<Long> userIds,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
  ) {
    Session session = new Session();
    session.setId(id);
    session.setName(name);
    session.setDate(date);
    session.setDescription(description);
    Teacher teacher = new Teacher();
    teacher.setId(1L);
    session.setTeacher(teacher);
    session.setUsers(
      userIds
        .stream()
        .map(SessionMapperTest::createUser)
        .collect(Collectors.toList())
    );
    session.setCreatedAt(createdAt);
    session.setUpdatedAt(updatedAt);
    return session;
  }

  private static Session createSessionWithoutTeacher(
    Long id,
    String name,
    Date date,
    String description,
    List<Long> userIds,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
  ) {
    Session session = createFullSession(
      id,
      name,
      date,
      description,
      null,
      userIds,
      createdAt,
      updatedAt
    );
    session.setTeacher(null);
    return session;
  }

  private static Session createSessionWithoutUsers(
    Long id,
    String name,
    Date date,
    String description,
    Long teacherId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
  ) {
    Session session = createFullSession(
      id,
      name,
      date,
      description,
      teacherId,
      Collections.emptyList(),
      createdAt,
      updatedAt
    );
    session.setUsers(Collections.emptyList());
    return session;
  }
}
