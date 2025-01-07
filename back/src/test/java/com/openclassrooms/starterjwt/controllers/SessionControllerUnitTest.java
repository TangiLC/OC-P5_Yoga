package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;

import org.junit.platform.suite.api.SuiteDisplayName;

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

@SuiteDisplayName("CONTROLLER")
@DisplayName("Unit tests for SessionController")
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

  @ParameterizedTest(name = "({index}) : {0} [{2}]")
  @CsvSource(
    {
      "Regular case : Successfully find session, 1, 200, valid",
      "Fail to find session : id unknown, 999, 404, valid",
      "Fail to find session : id invalid, invalid, 400, invalid",
    }
  )
  @DisplayName("Should handle FindById scenario ")
  void testFindByIdScenarios(
    String scenarioName,
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

  @ParameterizedTest(name = "({index}) : {0} [{3}]")
  @CsvSource(
    {
      "Regular case : successfully create a session, New Session, New Description, 200",
      //"Fail to create : invalid session name, , New Description, 400",
      //"Fail to create : invalid session Description, New Session, , 400",
      //"Fail to create : null data, , , 400"
    }
  )
  @DisplayName("Should handle different Create scenario ")
  void testCreateScenarios(
    String scenarioName,
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

  @ParameterizedTest(name = "({index}) : {0} [{3}]")
  @CsvSource(
    {
      "Regular case : successfully update session, 1, 'Updated Session', 200",
      "Fail to update : invalid session id, invalid, 'Updated Session', 400",
    }
  )
  @DisplayName("Should handle different Update scenario ")
  void testUpdateScenarios(
    String scenarioName,
    String inputId,
    String name,
    int expectedStatus
  ) {
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

  @ParameterizedTest(name = "({index}) : {0} [{2}]")
  @CsvSource(
    {
      "Regular case : successfully delete session, 1, 200",
      "Fail to delete : session id unknown, 999, 404",
      "Fail to delete : session id invalid, invalid, 400",
    }
  )
  @DisplayName("Should handle different Delete scenario ")
  void testDeleteScenarios(
    String scenarioName,
    String inputId,
    int expectedStatus
  ) {
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

  @ParameterizedTest(name = "({index}) : {0} [{3}]")
  @CsvSource(
    {
      "Regular case : user successfully participate to session, 1, 1, 200",
      //"Fail to participate : user unknown, 999, 1, 404", //->400
      "Fail to participate : invalid user id, 1, invalid, 400",
      "Fail to participate : invalid session id, invalid, 1, 400",
    }
  ) // TO DO : handle unknown user 404/400 ?
  @DisplayName("Should handle different Participate scenario ")
  void testParticipateScenarios(
    String scenarioName,
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

  @ParameterizedTest(name = "({index}) : {0} [{3}]")
  @CsvSource(
    {
      "Regular case : user successfully unparticipate to session, 1, 1, 200",
      //"Fail to unparticipate : session id unknown, 999, 1, 404", //-> 400
      "Fail to unparticipate : user id invalid,1, invalid, 400",
      "Fail to unparticipate : session id invalid,invalid, 1, 400",
    }
  )
  @DisplayName("Should handle different Unparticipate scenario ")
  void testUnparticipateScenarios(
    String scenarioName,
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
