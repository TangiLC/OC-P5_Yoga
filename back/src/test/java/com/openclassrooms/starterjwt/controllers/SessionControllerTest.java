package com.openclassrooms.starterjwt.controllers;

import static org.junit.jupiter.api.Assertions.*;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class SessionControllerTest {

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
  @DisplayName("Should return session by ID when it exists")
  void testFindById_Success() {
    Session session = new Session();
    session.setId(1L);
    SessionDto sessionDto = new SessionDto();
    sessionDto.setId(1L);
    when(sessionService.getById(1L)).thenReturn(session);
    when(sessionMapper.toDto(session)).thenReturn(sessionDto);

    ResponseEntity<?> response = sessionController.findById("1");

    assertNotNull(response); //should return a response status 200 with sessionDto in body
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(sessionDto, response.getBody());
  }

  @Test
  @DisplayName("Should return 404 when session not found by ID")
  void testFindById_NotFound() {
    when(sessionService.getById(1L)).thenReturn(null);

    ResponseEntity<?> response = sessionController.findById("1");

    assertNotNull(response); //should return a response status 404 if no session {id} found
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should return 400 for invalid ID format in findById")
  void testFindById_InvalidId() {
    ResponseEntity<?> response = sessionController.findById("&#*invalid");

    assertNotNull(response); //should return a response status 400 for invalid request
    assertEquals(400, response.getStatusCodeValue());
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

    assertNotNull(response); //should return status 200 and [sessionsDto] array in body
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(sessionDtos, response.getBody());
  }

  @Test
  @DisplayName("Should create a new session")
  void testCreate() {
    SessionDto sessionDto = new SessionDto();
    Session session = new Session();

    when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
    when(sessionService.create(session)).thenReturn(session);
    when(sessionMapper.toDto(session)).thenReturn(sessionDto);

    ResponseEntity<?> response = sessionController.create(sessionDto);

    assertNotNull(response); //should return status 200 and {sessionDto} in body
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(sessionDto, response.getBody());
  }

  @Test
  @DisplayName("Should update a session by ID")
  void testUpdate_Success() {
    SessionDto updatedSessionDto = new SessionDto();
    Session updatedSession = new Session();

    when(sessionMapper.toEntity(updatedSessionDto)).thenReturn(updatedSession);
    when(sessionService.update(1L, updatedSession)).thenReturn(updatedSession);
    when(sessionMapper.toDto(updatedSession)).thenReturn(updatedSessionDto);

    ResponseEntity<?> response = sessionController.update(
      "1",
      updatedSessionDto
    );

    assertNotNull(response); //should return status 200 and {SessionDto} in body
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(updatedSessionDto, response.getBody());
  }

  @Test
  @DisplayName("Should return 400 for invalid ID format in update")
  void testUpdate_InvalidId() {
    ResponseEntity<?> response = sessionController.update(
      "&@#invalid",
      new SessionDto()
    );

    assertNotNull(response); //should return a response status 400 for invalid request
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should delete a session by ID")
  void testDelete_Success() {
    Session session = new Session();

    when(sessionService.getById(1L)).thenReturn(session);
    doNothing().when(sessionService).delete(1L);

    ResponseEntity<?> response = sessionController.save("1");

    assertNotNull(response); //should response status 200 when delete {id} success
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  @DisplayName(
    "Should return 404 when trying to delete a session that does not exist"
  )
  void testDelete_NotFound() {
    when(sessionService.getById(1L)).thenReturn(null);

    ResponseEntity<?> response = sessionController.save("1");

    assertNotNull(response); //should response status 404 when tying to delete unknown {id}
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should return 400 for invalid ID format in delete")
  void testDelete_InvalidId() {
    ResponseEntity<?> response = sessionController.save("&@#invalid");

    assertNotNull(response); //should return a response status 400 for invalid request
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should allow a user to participate in a session")
  void testParticipate_Success() {
    doNothing().when(sessionService).participate(1L, 2L);

    ResponseEntity<?> response = sessionController.participate("1", "2");

    assertNotNull(response); //should return a response status 200 for successfully enrolling
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should return 400 for invalid ID format in participate")
  void testParticipate_InvalidId() {
    ResponseEntity<?> response = sessionController.participate("invalid", "2");

    assertNotNull(response); //should return a response status 400 for invalid request
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should allow a user to un-participate in a session")
  void testNoLongerParticipate_Success() {
    doNothing().when(sessionService).noLongerParticipate(1L, 2L);

    ResponseEntity<?> response = sessionController.noLongerParticipate(
      "1",
      "2"
    );

    assertNotNull(response); //should return a response status 200 for successfully un-enrolling
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  @DisplayName(
    "Should return 400 for invalid ID format in no longer participate"
  )
  void testNoLongerParticipate_InvalidId() {
    ResponseEntity<?> response = sessionController.noLongerParticipate(
      "invalid",
      "2"
    );

    assertNotNull(response); //should return a response status 400 for invalid request
    assertEquals(400, response.getStatusCodeValue());
  }
}
