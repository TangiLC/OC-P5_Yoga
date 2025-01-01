package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
  @CsvSource(
    {
      "Valid ID, 1, true, 200",
      "Non-existing ID, 999, false, 404",
      "Invalid ID format, invalid, false, 400",
    }
  )
  @DisplayName("Should handle different findById scenarios")
  void testFindByIdScenarios(
    String scenarioName,
    String inputId,
    boolean serviceResponseExists,
    int expectedStatus
  ) {
    User serviceResponse = serviceResponseExists ? new User() : null;
    UserDto mappedResponse = serviceResponseExists ? new UserDto() : null;

    if (!"invalid".equals(inputId)) {
      when(
        userService.findById(
          serviceResponseExists ? Long.parseLong(inputId) : null
        )
      )
        .thenReturn(serviceResponse);
      if (serviceResponseExists) {
        when(userMapper.toDto(serviceResponse)).thenReturn(mappedResponse);
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
  @CsvSource(
    {
      "Valid ID and authorized user, 1, true, test@example.com, 200",
      "Non-existing user ID, 999, false, , 404",
      "Invalid ID format, invalid, false, , 400",
      //"Unauthorized user, 1, true, unauthorized@example.com, 401",
    }
  )
  @DisplayName("Should handle different delete scenarios")
  void testDeleteScenarios(
    String scenarioName,
    String inputId,
    boolean serviceResponseExists,
    String authenticatedEmail,
    int expectedStatus
  ) {
    User serviceResponse = serviceResponseExists ? new User() : null;

    if (serviceResponseExists) {
      serviceResponse.setId(1L);
      serviceResponse.setEmail(authenticatedEmail);
    }

    if (!"invalid".equals(inputId)) {
      when(
        userService.findById(
          serviceResponseExists ? Long.parseLong(inputId) : null
        )
      )
        .thenReturn(serviceResponse);

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
}
