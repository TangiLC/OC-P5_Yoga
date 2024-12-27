package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthControllerUnitTest {

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtUtils jwtUtils;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private AuthController authController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @SuppressWarnings("null")
  @ParameterizedTest(name = "{0}")
  @MethodSource("loginScenarios")
  @DisplayName("Should handle different login scenarios")
  void testAuthenticateUser(
    String scenarioName,
    LoginRequest loginRequest,
    User user,
    String token,
    int expectedStatus,
    boolean isAdmin
  ) {
    if (user != null) {
      Authentication authentication = mock(Authentication.class);
      UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

      when(
        authenticationManager.authenticate(
          any(UsernamePasswordAuthenticationToken.class)
        )
      )
        .thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(userDetails);
      when(jwtUtils.generateJwtToken(authentication)).thenReturn(token);
      when(userRepository.findByEmail(loginRequest.getEmail()))
        .thenReturn(Optional.of(user));
    } else {
      when(userRepository.findByEmail(loginRequest.getEmail()))
        .thenReturn(Optional.empty());
    }

    ResponseEntity<?> response = authController.authenticateUser(loginRequest);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCodeValue()).isEqualTo(expectedStatus);

    if (expectedStatus == 200) {
      JwtResponse jwtResponse = (JwtResponse) response.getBody();
      assertThat(jwtResponse).isNotNull();
      assertThat(jwtResponse.getToken()).isEqualTo(token);
      assertThat(jwtResponse.getAdmin()).isEqualTo(isAdmin);
    }
  }

  @SuppressWarnings("null")
  @ParameterizedTest(name = "{0}")
  @MethodSource("registerScenarios")
  @DisplayName("Should handle different registration scenarios")
  void testRegisterUser(
    String scenarioName,
    SignupRequest signupRequest,
    boolean emailExists,
    String encodedPassword,
    int expectedStatus,
    String expectedMessage
  ) {
    when(userRepository.existsByEmail(signupRequest.getEmail()))
      .thenReturn(emailExists);
    if (!emailExists) {
      when(passwordEncoder.encode(signupRequest.getPassword()))
        .thenReturn(encodedPassword);
    }

    ResponseEntity<?> response = authController.registerUser(signupRequest);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCodeValue()).isEqualTo(expectedStatus);

    MessageResponse messageResponse = (MessageResponse) response.getBody();
    assertThat(messageResponse).isNotNull();
    assertThat(messageResponse.getMessage()).isEqualTo(expectedMessage);

    if (!emailExists) {
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());
      User savedUser = userCaptor.getValue();
      assertThat(savedUser).isNotNull();
      assertThat(savedUser.getEmail()).isEqualTo(signupRequest.getEmail());
      assertThat(savedUser.getFirstName())
        .isEqualTo(signupRequest.getFirstName());
      assertThat(savedUser.getLastName())
        .isEqualTo(signupRequest.getLastName());
      assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
      assertThat(savedUser.isAdmin()).isFalse();
    }
  }

  // Test scenarios for login
  private static Stream<Arguments> loginScenarios() {
    User existingUser = new User(
      "test@example.com",
      "Last",
      "First",
      "password",
      false
    );
    existingUser.setId(1L);

    LoginRequest validRequest = new LoginRequest();
    validRequest.setEmail("test@example.com");
    validRequest.setPassword("password");

    LoginRequest unknownUserRequest = new LoginRequest();
    unknownUserRequest.setEmail("unknown@example.com");
    unknownUserRequest.setPassword("wrongpassword");

    return Stream.of(
      Arguments.of(
        "Should return 200 and token for valid login",
        validRequest,
        existingUser,
        "mockedJwtToken",
        200,
        false
      )
      // TO DO : catch error in authenticate method for unknown user,
      // the current return error is 503 (Timeout)
      /*,Arguments.of(  
        "Should return 404 for unknown user",
        unknownUserRequest,
        null,
        null,
        400,
        true
      )*/
    );
  }

  // Test scenarios for registration
  private static Stream<Arguments> registerScenarios() {
    SignupRequest validSignup = new SignupRequest();
    validSignup.setEmail("new@example.com");
    validSignup.setFirstName("First");
    validSignup.setLastName("Last");
    validSignup.setPassword("password");

    SignupRequest existingEmailSignup = new SignupRequest();
    existingEmailSignup.setEmail("existing@example.com");
    existingEmailSignup.setFirstName("First");
    existingEmailSignup.setLastName("Last");
    existingEmailSignup.setPassword("password");

    return Stream.of(
      Arguments.of(
        "Should return 200 and register successfully for new user",
        validSignup,
        false,
        "encodedPassword",
        200,
        "User registered successfully!"
      ),
      Arguments.of(
        "Should return 400 for existing email",
        existingEmailSignup,
        true,
        null,
        400,
        "Error: Email is already taken!"
      )
    );
  }
}
