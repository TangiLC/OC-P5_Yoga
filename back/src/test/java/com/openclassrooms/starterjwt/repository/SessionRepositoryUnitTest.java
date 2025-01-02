package com.openclassrooms.starterjwt.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.models.Session;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuiteDisplayName("REPOSITORY")
@DisplayName("Unit tests for SessionRepository")
class SessionRepositoryUnitTest {

  @Mock
  private SessionRepository sessionRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @ParameterizedTest(name = "({index}) : {0} [{2}]")
  @CsvSource(
    { "Existing session id, 1, true", "Non-existing session id, 999, false" }
  )
  @DisplayName("Should find session by id ")
  void testFindById(String scenarioName, Long sessionId, boolean shouldExist) {
    Session session = new Session();
    session.setId(1L);
    session.setName("Yoga Class");

    when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
    when(sessionRepository.findById(999L)).thenReturn(Optional.empty());

    Optional<Session> result = sessionRepository.findById(sessionId);

    if (shouldExist) {
      assertThat(result).isPresent();
      assertThat(result.get().getId()).isEqualTo(sessionId);
    } else {
      assertThat(result).isEmpty();
    }
    verify(sessionRepository, times(1)).findById(sessionId);
  }

  @ParameterizedTest(name = "({index}) : {0} [{2}]")
  @CsvSource(
    { "Existing session id, 1, true", "Non-existing session id, 999, false" }
  )
  @DisplayName("Should check if session exists by id ")
  void testExistsById(
    String scenarioName,
    Long sessionId,
    boolean shouldExist
  ) {
    when(sessionRepository.existsById(1L)).thenReturn(true);
    when(sessionRepository.existsById(999L)).thenReturn(false);

    boolean exists = sessionRepository.existsById(sessionId);

    assertThat(exists).isEqualTo(shouldExist);
    verify(sessionRepository, times(1)).existsById(sessionId);
  }
}
