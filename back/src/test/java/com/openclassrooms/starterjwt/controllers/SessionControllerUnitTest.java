package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class SessionControllerUnitTest {

  @Mock
  private SessionService sessionService;

  @Mock
  private SessionMapper sessionMapper;

  @InjectMocks
  private SessionController sessionController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should return all sessions")
  void testFindAll() {
    List<Session> sessions = Arrays.asList(new Session(), new Session());
    List<SessionDto> sessionDtos = Arrays.asList(
      new SessionDto(),
      new SessionDto()
    );

    when(sessionService.findAll()).thenReturn(sessions);
    when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

    ResponseEntity<?> response = sessionController.findAll();

    assertThat(response).isNotNull();
    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getBody()).isEqualTo(sessionDtos);
  }

  @ParameterizedTest(name = "Find by ID - {0}")
  @CsvSource({ "1, 200, valid", "999, 404, valid", "invalid, 400, invalid" })
  @DisplayName("Should handle different findById scenarios")
  void testFindByIdScenarios(
    String inputId,
    int expectedStatus,
    String description
  ) {
    Session session = new Session();
    session.setId(1L);
    SessionDto sessionDto = new SessionDto();
    sessionDto.setId(1L);

    if (!"invalid".equals(inputId)) {
      when(sessionService.getById(Long.valueOf(inputId)))
        .thenReturn(expectedStatus == 200 ? session : null);
      if (expectedStatus == 200) {
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);
      }
    }

    ResponseEntity<?> response = sessionController.findById(inputId);

    assertThat(response.getStatusCodeValue()).isEqualTo(expectedStatus);
    if (expectedStatus == 200) {
      assertThat(response.getBody()).isEqualTo(sessionDto);
    }
  }

  @ParameterizedTest(name = "Create - {0}")
  @CsvSource({ "'New Session', 'New Description', 200" })
  @DisplayName("Should handle different create scenarios")
  void testCreateScenarios(
    String name,
    String description,
    int expectedStatus
  ) {
    SessionDto inputDto = new SessionDto();
    inputDto.setName(name);
    inputDto.setDescription(description);

    Session mappedEntity = new Session();
    mappedEntity.setName(name);
    mappedEntity.setDescription(description);

    Session serviceResponse = new Session();
    serviceResponse.setId(1L);
    serviceResponse.setName(name);
    serviceResponse.setDescription(description);

    SessionDto mappedResponse = new SessionDto();
    mappedResponse.setId(1L);
    mappedResponse.setName(name);
    mappedResponse.setDescription(description);

    when(sessionMapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(sessionService.create(mappedEntity)).thenReturn(serviceResponse);
    when(sessionMapper.toDto(serviceResponse)).thenReturn(mappedResponse);

    ResponseEntity<?> response = sessionController.create(inputDto);

    assertThat(response.getStatusCodeValue()).isEqualTo(expectedStatus);
    if (expectedStatus == 200) {
      assertThat(response.getBody()).isEqualTo(mappedResponse);
    }
  }

  @ParameterizedTest(name = "Update - {0}")
  @CsvSource({ "1, 'Updated Session', 200", "invalid, 'Updated Session', 400" })
  @DisplayName("Should handle different update scenarios")
  void testUpdateScenarios(String inputId, String name, int expectedStatus) {
    SessionDto inputDto = new SessionDto();
    inputDto.setName(name);

    Session mappedEntity = new Session();
    mappedEntity.setName(name);

    Session serviceResponse = new Session();
    serviceResponse.setId(1L);
    serviceResponse.setName(name);

    SessionDto mappedResponse = new SessionDto();
    mappedResponse.setId(1L);
    mappedResponse.setName(name);

    if (!"invalid".equals(inputId)) {
      when(sessionMapper.toEntity(inputDto)).thenReturn(mappedEntity);
      when(
        sessionService.update(eq(Long.parseLong(inputId)), any(Session.class))
      )
        .thenReturn(serviceResponse);
      if (expectedStatus == 200) {
        when(sessionMapper.toDto(serviceResponse)).thenReturn(mappedResponse);
      }
    }

    ResponseEntity<?> response = sessionController.update(inputId, inputDto);

    assertThat(response.getStatusCodeValue()).isEqualTo(expectedStatus);
    if (expectedStatus == 200) {
      assertThat(response.getBody()).isEqualTo(mappedResponse);
    }
  }

  @ParameterizedTest(name = "Delete - {0}")
  @CsvSource({ "1, 200", "999, 404", "invalid, 400" })
  @DisplayName("Should handle different delete scenarios")
  void testDeleteScenarios(String inputId, int expectedStatus) {
    Session existingSession = new Session();
    existingSession.setId(1L);

    if (!"invalid".equals(inputId)) {
      when(sessionService.getById(Long.valueOf(inputId)))
        .thenReturn(expectedStatus == 200 ? existingSession : null);
      if (expectedStatus == 200) {
        doNothing().when(sessionService).delete(Long.valueOf(inputId));
      }
    }

    ResponseEntity<?> response = sessionController.save(inputId);

    assertThat(response.getStatusCodeValue()).isEqualTo(expectedStatus);
  }

  @ParameterizedTest(name = "Participate - {0}")
  @CsvSource(
    {
      "1, 1, 200",
      //"999, 1, 404",
      "1, invalid, 400",
      "invalid, 1, 400",
    }
  )
  @DisplayName("Should handle different participate scenarios")
  void testParticipateScenarios(
    String sessionId,
    String userId,
    int expectedStatus
  ) {
    if (!"invalid".equals(sessionId) && !"invalid".equals(userId)) {
      doNothing()
        .when(sessionService)
        .participate(Long.parseLong(sessionId), Long.parseLong(userId));
    }

    ResponseEntity<?> response = sessionController.participate(
      sessionId,
      userId
    );

    assertThat(response.getStatusCodeValue()).isEqualTo(expectedStatus);
  }

  @ParameterizedTest(name = "Unparticipate - {0}")
  @CsvSource(
    {
      "1, 1, 200",
      //"999, 1, 404",
      "1, invalid, 400",
      "invalid, 1, 400",
    }
  )
  @DisplayName("Should handle different unparticipate scenarios")
  void testUnparticipateScenarios(
    String sessionId,
    String userId,
    int expectedStatus
  ) {
    if (!"invalid".equals(sessionId) && !"invalid".equals(userId)) {
      doNothing()
        .when(sessionService)
        .noLongerParticipate(Long.parseLong(sessionId), Long.parseLong(userId));
    }

    ResponseEntity<?> response = sessionController.noLongerParticipate(
      sessionId,
      userId
    );

    assertThat(response.getStatusCodeValue()).isEqualTo(expectedStatus);
  }
}
