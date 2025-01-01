package com.openclassrooms.starterjwt.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.models.Session;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SessionRepositoryUnitTest {

  @Mock
  private SessionRepository sessionRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @ParameterizedTest
  @CsvSource(
    {
      "1, true", // Existing session ID
      "999, false", // Non-existing session ID
    }
  )
  @DisplayName("Should find session by ID")
  void testFindById(Long sessionId, boolean shouldExist) {
    // Prepare
    Session session = new Session();
    session.setId(1L);
    session.setName("Yoga Class");

    // Mock repository behavior
    when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
    when(sessionRepository.findById(999L)).thenReturn(Optional.empty());

    // Execute
    Optional<Session> result = sessionRepository.findById(sessionId);

    // Verify
    if (shouldExist) {
      assertThat(result).isPresent();
      assertThat(result.get().getId()).isEqualTo(sessionId);
    } else {
      assertThat(result).isEmpty();
    }
    verify(sessionRepository, times(1)).findById(sessionId);
  }

  @ParameterizedTest
  @CsvSource(
    {
      "1, true", // Existing session ID
      "999, false", // Non-existing session ID
    }
  )
  @DisplayName("Should check if session exists by ID")
  void testExistsById(Long sessionId, boolean shouldExist) {
    // Mock repository behavior
    when(sessionRepository.existsById(1L)).thenReturn(true);
    when(sessionRepository.existsById(999L)).thenReturn(false);

    // Execute
    boolean exists = sessionRepository.existsById(sessionId);

    // Verify
    assertThat(exists).isEqualTo(shouldExist);
    verify(sessionRepository, times(1)).existsById(sessionId);
  }
}
