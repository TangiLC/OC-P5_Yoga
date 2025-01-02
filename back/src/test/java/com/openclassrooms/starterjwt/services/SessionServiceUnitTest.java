package com.openclassrooms.starterjwt.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.platform.suite.api.SuiteDisplayName;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuiteDisplayName("SERVICE")
@DisplayName("Unit tests for SessionService")
class SessionServiceUnitTest {

  @Mock
  private SessionRepository sessionRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private SessionService sessionService;

  public SessionServiceUnitTest() {
    MockitoAnnotations.openMocks(this);
  }

  private static Session createSession(Long id, List<User> users) {
    Session session = new Session();
    session.setId(id);
    session.setUsers(
      users != null ? new ArrayList<>(users) : new ArrayList<>()
    );
    return session;
  }

  private static User createUser(Long id) {
    User user = new User();
    user.setId(id);
    return user;
  }

  @ParameterizedTest(name = "({index}) : {0}")
  @CsvSource(
    {
      "Regular case: session and user exist, 1, 10, true, true, false",
      "Fail : Session does not exist, 2, 20, false, true, true",
      "Fail : User does not exist, 3, 30, true, false, true",
      "Fail : User already participating, 4, 40, true, true, true",
    }
  )
  @DisplayName("Should handle different participate scenario ")
  void participate_ShouldHandleScenarios(
    String scenarioName,
    Long sessionId,
    Long userId,
    boolean sessionExists,
    boolean userExists,
    boolean expectException
  ) {
    Session session = sessionExists
      ? createSession(sessionId, List.of())
      : null;
    User user = userExists ? createUser(userId) : null;

    if (sessionExists) {
      when(sessionRepository.findById(sessionId))
        .thenReturn(Optional.of(session));
    } else {
      when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());
    }

    if (userExists) {
      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    } else {
      when(userRepository.findById(userId)).thenReturn(Optional.empty());
    }

    if (!expectException) {
      sessionService.participate(sessionId, userId);
      assertThat(session.getUsers()).contains(user);
      verify(sessionRepository, times(1)).save(session);
    } else {
      if (!sessionExists) {
        assertThatThrownBy(() -> sessionService.participate(sessionId, userId))
          .isInstanceOf(NotFoundException.class)
          .hasMessageContaining(
            "Not found: the data you are looking for is unavailable."
          );
      } else if (!userExists) {
        assertThatThrownBy(() -> sessionService.participate(sessionId, userId))
          .isInstanceOf(NotFoundException.class)
          .hasMessageContaining(
            "Not found: the data you are looking for is unavailable."
          );
      }
      verify(sessionRepository, never()).save(any(Session.class));
    }
  }

  @ParameterizedTest(name = "({index}) : {0} [{5}]")
  @CsvSource(
    {
      "Regular case: User participates, 1, 10, true, true, false",
      "Fail : User does not participate, 1, 10, true, false, true",
      "Fail : Session does not exist, 2, 20, false, false, true",
    }
  )
  @DisplayName("Should handle different noLongerParticipate scenario ")
  void noLongerParticipate_ShouldHandleScenarios(
    String scenarioName,
    Long sessionId,
    Long userId,
    boolean sessionExists,
    boolean userParticipates,
    boolean expectException
  ) {
    Session session = sessionExists
      ? createSession(
        sessionId,
        userParticipates ? List.of(createUser(userId)) : List.of()
      )
      : null;

    if (sessionExists) {
      when(sessionRepository.findById(sessionId))
        .thenReturn(Optional.of(session));
    } else {
      when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());
    }

    if (!expectException) {
      sessionService.noLongerParticipate(sessionId, userId);
      assertThat(session.getUsers()).doesNotContain(createUser(userId));
      verify(sessionRepository, times(1)).save(session);
    } else {
      if (!sessionExists) {
        assertThatThrownBy(() ->
            sessionService.noLongerParticipate(sessionId, userId)
          )
          .isInstanceOf(NotFoundException.class);
      } else {
        assertThatThrownBy(() ->
            sessionService.noLongerParticipate(sessionId, userId)
          )
          .isInstanceOf(BadRequestException.class);
      }
      verify(sessionRepository, never()).save(any(Session.class));
    }
  }

  @Test
  @DisplayName("Should return all sessions when findAll is called")
  void findAll_ShouldReturnAllSessions() {
    List<Session> sessions = List.of(
      createSession(1L, null),
      createSession(2L, null)
    );
    when(sessionRepository.findAll()).thenReturn(sessions);

    List<Session> result = sessionService.findAll();

    assertThat(result).isNotNull().hasSize(2).containsAll(sessions);
  }

  @Test
  @DisplayName("Should create a new session successfully")
  void create_ShouldCreateSessionSuccessfully() {
    Session newSession = new Session();
    newSession.setName("New Session");

    Session savedSession = new Session();
    savedSession.setId(1L);
    savedSession.setName("New Session");

    when(sessionRepository.save(newSession)).thenReturn(savedSession);

    Session result = sessionService.create(newSession);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getName()).isEqualTo("New Session");

    verify(sessionRepository, times(1)).save(newSession);
  }

  @Test
  @DisplayName("Should update an existing session successfully")
  void update_ShouldUpdateSessionSuccessfully() {
    Long sessionId = 1L;
    Session existingSession = new Session();
    existingSession.setId(sessionId);
    existingSession.setName("Old Session");

    Session updatedSession = new Session();
    updatedSession.setId(sessionId);
    updatedSession.setName("Updated Session");

    when(sessionRepository.findById(sessionId))
      .thenReturn(Optional.of(existingSession));
    when(sessionRepository.save(updatedSession)).thenReturn(updatedSession);

    Session result = sessionService.update(sessionId, updatedSession);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(sessionId);
    assertThat(result.getName()).isEqualTo("Updated Session");

    verify(sessionRepository, times(1)).save(updatedSession);
  }
}
