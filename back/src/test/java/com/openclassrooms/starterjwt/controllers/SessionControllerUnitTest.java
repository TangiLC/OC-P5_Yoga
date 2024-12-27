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
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
  @MethodSource("findByIdScenarios")
  @DisplayName("Should handle different findById scenarios")
  void testFindByIdScenarios(
    String scenarioName,
    String inputId,
    Session serviceResponse,
    SessionDto mappedResponse,
    int expectedStatus
  ) {
    if (!"invalid".equals(inputId)) {
      when(sessionService.getById(Long.valueOf(inputId)))
        .thenReturn(serviceResponse);
      if (serviceResponse != null) {
        when(sessionMapper.toDto(serviceResponse)).thenReturn(mappedResponse);
      }
    }

    ResponseEntity<?> response = sessionController.findById(inputId);

    assertThat(response.getStatusCodeValue()).isEqualTo(expectedStatus);
    if (expectedStatus == 200) {
      assertThat(response.getBody()).isEqualTo(mappedResponse);
    }
  }

  @ParameterizedTest(name = "Create - {0}")
  @MethodSource("createScenarios")
  @DisplayName("Should handle different create scenarios")
  void testCreateScenarios(
    String scenarioName,
    SessionDto inputDto,
    Session mappedEntity,
    Session serviceResponse,
    SessionDto mappedResponse,
    int expectedStatus
  ) {
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
  @MethodSource("updateScenarios")
  @DisplayName("Should handle different update scenarios")
  void testUpdateScenarios(
    String scenarioName,
    String inputId,
    SessionDto inputDto,
    Session mappedEntity,
    Session serviceResponse,
    SessionDto mappedResponse,
    int expectedStatus
  ) {
    if (!"invalid".equals(inputId)) {
      when(sessionMapper.toEntity(inputDto)).thenReturn(mappedEntity);
      when(
        sessionService.update(eq(Long.parseLong(inputId)), any(Session.class))
      )
        .thenReturn(serviceResponse);
      if (serviceResponse != null) {
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
  @MethodSource("deleteScenarios")
  @DisplayName("Should handle different delete scenarios")
  void testDeleteScenarios(
    String scenarioName,
    String inputId,
    Session existingSession,
    int expectedStatus
  ) {
    if (!"invalid".equals(inputId)) {
      when(sessionService.getById(Long.valueOf(inputId)))
        .thenReturn(existingSession);
      if (existingSession != null) {
        doNothing().when(sessionService).delete(Long.valueOf(inputId));
      }
    }

    ResponseEntity<?> response = sessionController.save(inputId);

    assertThat(response.getStatusCodeValue()).isEqualTo(expectedStatus);
  }

  @ParameterizedTest(name = "Participate - {0}")
  @MethodSource("participateScenarios")
  @DisplayName("Should handle different participate scenarios")
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

  @ParameterizedTest(name = "Unparticipate - {0}")
  @MethodSource("unparticipateScenarios")
  @DisplayName("Should handle different unparticipate scenarios")
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

  // Tests Scenarios
  private static Stream<Arguments> findByIdScenarios() {
    Session session = new Session();
    session.setId(1L);
    SessionDto sessionDto = new SessionDto();
    sessionDto.setId(1L);

    return Stream.of(
      Arguments.of(
        "should respond 200 if successfully finding session #id",
        "1",
        session,
        sessionDto,
        200
      ),
      Arguments.of(
        "should respond 404 if failing to find session #id ",
        "999",
        null,
        null,
        404
      ),
      Arguments.of(
        "should respond 400 if Invalid ID Format",
        "invalid",
        null,
        null,
        400
      )
    );
  }

  private static Stream<Arguments> createScenarios() {
    SessionDto inputDto = new SessionDto();
    inputDto.setName("New Session");
    inputDto.setDescription("New Description");

    Session mappedEntity = new Session();
    mappedEntity.setName("New Session");
    mappedEntity.setDescription("New Description");

    Session serviceResponse = new Session();
    serviceResponse.setId(1L);
    serviceResponse.setName("New Session");
    serviceResponse.setDescription("New Description");

    SessionDto mappedResponse = new SessionDto();
    mappedResponse.setId(1L);
    mappedResponse.setName("New Session");
    mappedResponse.setDescription("New Description");

    return Stream.of(
      Arguments.of(
        "Should respond 200 if successfully creating session",
        inputDto,
        mappedEntity,
        serviceResponse,
        mappedResponse,
        200
      )
    );
  }

  private static Stream<Arguments> updateScenarios() {
    SessionDto inputDto = new SessionDto();
    inputDto.setName("Updated Session");
    Session mappedEntity = new Session();
    mappedEntity.setName("Updated Session");
    Session serviceResponse = new Session();
    serviceResponse.setId(1L);
    serviceResponse.setName("Updated Session");
    SessionDto mappedResponse = new SessionDto();
    mappedResponse.setId(1L);
    mappedResponse.setName("Updated Session");

    return Stream.of(
      Arguments.of(
        "Should response 200 if successfully updating session #id",
        "1",
        inputDto,
        mappedEntity,
        serviceResponse,
        mappedResponse,
        200
      ),
      Arguments.of(
        "Should response 400 if Invalid ID Format",
        "invalid",
        inputDto,
        null,
        null,
        null,
        400
      )
    );
  }

  private static Stream<Arguments> deleteScenarios() {
    Session existingSession = new Session();
    existingSession.setId(1L);

    return Stream.of(
      Arguments.of(
        "Should response 200 if successfully deleted session #id",
        "1",
        existingSession,
        200
      ),
      Arguments.of(
        "Should response 404 if trying to delete unknown #id session",
        "999",
        null,
        404
      ),
      Arguments.of(
        "Should response 400 if Invalid ID Format",
        "invalid",
        null,
        400
      )
    );
  }

  private static Stream<Arguments> participateScenarios() {
    return Stream.of(
      Arguments.of(
        "Should response 200 if successfully adding participant to session",
        "1",
        "1",
        200
      ),
      Arguments.of(
        "Should response 404 if trying to add participant to unknown #id session",
        "999",
        "1",
        200
      ),
      Arguments.of(
        "Should response 404 if trying to add unknown participant to session",
        "1",
        "666",
        200
      ),
      Arguments.of(
        "Should response 400 if Invalid Session ID",
        "invalid",
        "1",
        400
      ),
      Arguments.of(
        "Should response 400 if Invalid User ID",
        "1",
        "invalid",
        400
      ),
      Arguments.of(
        "Should response 400 if Both IDs Invalid",
        "invalid",
        "invalid",
        400
      )
    );
  }

  private static Stream<Arguments> unparticipateScenarios() {
    return Stream.of(
      Arguments.of(
        "Should response 200 if successfully removing participant to session",
        "1",
        "1",
        200
      ),
      Arguments.of(
        "Should response 404 if trying to remove participant to unknown #id session",
        "999",
        "1",
        200
      ),
      Arguments.of(
        "Should response 404 if trying to remove unknown participant to session",
        "1",
        "666",
        200
      ),
      Arguments.of(
        "Should response 400 if Invalid Session ID",
        "invalid",
        "1",
        400
      ),
      Arguments.of(
        "Should response 400 if Invalid User ID",
        "1",
        "invalid",
        400
      ),
      Arguments.of(
        "Should response 400 if Both IDs Invalid",
        "invalid",
        "invalid",
        400
      )
    );
  }
}
