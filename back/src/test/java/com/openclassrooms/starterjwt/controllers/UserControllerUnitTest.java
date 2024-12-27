package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

class UserControllerUnitTest {

  @Mock
  private UserService userService;

  @Mock
  private UserMapper userMapper;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private UserController userController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    SecurityContextHolder.setContext(securityContext);
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("findByIdScenarios")
  @DisplayName("Should handle different findById scenarios")
  void testFindByIdScenarios(
    String scenarioName,
    String inputId,
    User serviceResponse,
    UserDto mappedResponse,
    int expectedStatus
  ) {
    if (!"invalid".equals(inputId)) {
      if (serviceResponse != null) {
        when(userService.findById(Long.parseLong(inputId)))
          .thenReturn(serviceResponse);
        when(userMapper.toDto(serviceResponse)).thenReturn(mappedResponse);
      } else {
        when(userService.findById(Long.parseLong(inputId))).thenReturn(null);
      }
    }

    ResponseEntity<?> response = userController.findById(inputId);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCodeValue()).isEqualTo(expectedStatus);
    if (expectedStatus == 200) {
      assertThat(response.getBody()).isEqualTo(mappedResponse);
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("deleteScenarios")
  @DisplayName("Should handle different delete scenarios")
  void testDeleteScenarios(
    String scenarioName,
    String inputId,
    User serviceResponse,
    String authenticatedEmail,
    int expectedStatus
  ) {
    if (!"invalid".equals(inputId)) {
      if (serviceResponse != null) {
        when(userService.findById(Long.parseLong(inputId)))
          .thenReturn(serviceResponse);
      } else {
        when(userService.findById(Long.parseLong(inputId))).thenReturn(null);
      }

      if (authenticatedEmail != null) {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(authenticatedEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
      }
    }

    ResponseEntity<?> response = userController.save(inputId);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCodeValue()).isEqualTo(expectedStatus);
  }

  // Test Scenarios
  private static Stream<Arguments> findByIdScenarios() {
    User user = new User();
    user.setId(1L);
    UserDto userDto = new UserDto();
    userDto.setId(1L);

    return Stream.of(
      Arguments.of(
        "Should return 200 and userDto for valid ID",
        "1",
        user,
        userDto,
        200
      ),
      Arguments.of(
        "Should return 404 for non-existing ID",
        "999",
        null,
        null,
        404
      ),
      Arguments.of(
        "Should return 400 for invalid ID format",
        "invalid",
        null,
        null,
        400
      )
    );
  }

  private static Stream<Arguments> deleteScenarios() {
    User user = new User();
    user.setId(1L);
    user.setEmail("test@example.com");

    return Stream.of(
      Arguments.of(
        "Should return 200 for valid ID and authorized user",
        "1",
        user,
        "test@example.com",
        200
      ),
      Arguments.of(
        "Should return 404 for non-existing user ID",
        "999",
        null,
        null,
        404
      ),
      Arguments.of(
        "Should return 400 for invalid ID format",
        "invalid",
        null,
        null,
        400
      ),
      Arguments.of(
        "Should return 401 for unauthorized user",
        "1",
        user,
        "unauthorized@example.com",
        401
      )
    );
  }
}
