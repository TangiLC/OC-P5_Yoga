package com.openclassrooms.starterjwt.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuiteDisplayName("MAPPER")
@DisplayName("Unit tests for SessionsMapper")
class SessionsMapperUnitTest {

  @Mock
  private TeacherService teacherService;

  @Mock
  private UserService userService;

  @InjectMocks
  private SessionMapper sessionMapper = Mappers.getMapper(SessionMapper.class);

  private static final Date TEST_DATE = new Date();
  private static final LocalDateTime TEST_DATETIME = LocalDateTime.now();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should map correctly toDto List")
  void toDtoList_ShouldMapCorrectly() {
    List<Session> sessions = List.of(
      createFullSession(1L, "Session 1", "Description 1"),
      createFullSession(2L, "Session 2", "Description 2")
    );

    List<SessionDto> expectedSessionDtos = List.of(
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
      new SessionDto(
        2L,
        "Session 2",
        TEST_DATE,
        2L,
        "Description 2",
        Arrays.asList(13L),
        TEST_DATETIME,
        TEST_DATETIME
      )
    );

    List<SessionDto> result = sessionMapper.toDto(sessions);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(expectedSessionDtos.size());

    for (int i = 0; i < expectedSessionDtos.size(); i++) {
      SessionDto expected = expectedSessionDtos.get(i);
      SessionDto actual = result.get(i);

      assertThat(actual.getId()).isEqualTo(expected.getId());
      assertThat(actual.getName()).isEqualTo(expected.getName());
      assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
      assertThat(actual.getTeacher_id()).isEqualTo(expected.getTeacher_id());
      assertThat(actual.getUsers()).isEqualTo(expected.getUsers());
      assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
      assertThat(actual.getUpdatedAt()).isEqualTo(expected.getUpdatedAt());
    }
  }

  @Test
  @DisplayName("Should map correctly toEntity List")
  void toEntityList_ShouldMapCorrectly() {
    Teacher mockedTeacher = new Teacher(1L, "John", "Doe", null, null);
    when(teacherService.findById(1L)).thenReturn(mockedTeacher);

    User mockedUser1 = new User(
      11L,
      "user1@test.com",
      "John",
      "Smith",
      "password",
      false,
      TEST_DATETIME,
      TEST_DATETIME
    );
    User mockedUser2 = new User(
      12L,
      "user2@test.com",
      "Jane",
      "Doe",
      "password",
      false,
      TEST_DATETIME,
      TEST_DATETIME
    );
    when(userService.findById(11L)).thenReturn(mockedUser1);
    when(userService.findById(12L)).thenReturn(mockedUser2);

    List<SessionDto> sessionDtos = List.of(
      new SessionDto(
        1L,
        "Session 1",
        TEST_DATE,
        1L,
        "Description 1",
        Arrays.asList(11L, 12L),
        TEST_DATETIME,
        TEST_DATETIME
      )
    );

    List<Session> expectedSessions = List.of(
      createFullSession(1L, "Session 1", "Description 1")
    );

    List<Session> result = sessionMapper.toEntity(sessionDtos);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(expectedSessions.size());

    for (int i = 0; i < expectedSessions.size(); i++) {
      Session expected = expectedSessions.get(i);
      Session actual = result.get(i);

      assertThat(actual.getId()).isEqualTo(expected.getId());
      assertThat(actual.getName()).isEqualTo(expected.getName());
      assertThat(actual.getDate()).isEqualTo(TEST_DATE);
      assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
      assertThat(actual.getTeacher().getId())
        .isEqualTo(expected.getTeacher().getId());
      assertThat(actual.getUsers()).hasSize(expected.getUsers().size());

      assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
      assertThat(actual.getUpdatedAt()).isEqualTo(expected.getUpdatedAt());
    }
  }

  private static Session createFullSession(
    Long id,
    String name,
    String description
  ) {
    Session session = new Session();
    session.setId(id);
    session.setName(name);
    session.setDate(TEST_DATE);
    session.setDescription(description);
    session.setCreatedAt(TEST_DATETIME);
    session.setUpdatedAt(TEST_DATETIME);

    Teacher teacher = new Teacher();
    teacher.setId(id);
    session.setTeacher(teacher);

    if (id == 1L) {
      session.setUsers(List.of(createUser(11L), createUser(12L)));
    } else {
      session.setUsers(List.of(createUser(13L)));
    }

    return session;
  }

  private static User createUser(Long id) {
    User user = new User();
    user.setId(id);
    return user;
  }
}
