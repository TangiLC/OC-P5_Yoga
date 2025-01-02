package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import org.junit.platform.suite.api.SuiteDisplayName;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SuiteDisplayName("CONTROLLER")
@DisplayName("Unit tests for AuthController")
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

  @ParameterizedTest(name = "({index}) : {0} [{4}]")
  @CsvSource(
    {
      "Valid login, test@example.com, password, mockedJwtToken, 200, false",
      "Unknown user, unknown@example.com, wrongpassword, , 404, false",
    }
  )
  @DisplayName("Should handle Login scenario ")
  void testAuthenticateUser(
    String scenarioName,
    String email,
    String password,
    String token,
    int expectedStatus,
    boolean isAdmin
  ) {
    assertThat(email).isNotNull();
    assertThat(password).isNotNull();

    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail(email);
    loginRequest.setPassword(password);

    User user = null;
    if (expectedStatus == 200) {
      user = new User(email, "Last", "First", password, isAdmin);
      user.setId(1L);

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
      when(userDetails.getUsername()).thenReturn(email);
      when(userDetails.getId()).thenReturn(1L);
      when(userDetails.getFirstName()).thenReturn("First");
      when(userDetails.getLastName()).thenReturn("Last");
      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    } else {
      when(
        authenticationManager.authenticate(
          any(UsernamePasswordAuthenticationToken.class)
        )
      )
        .thenThrow(
          new org.springframework.security.authentication.BadCredentialsException(
            "Bad credentials"
          )
        );
      when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
    }

    if (expectedStatus == 200) {
      ResponseEntity<?> response = authController.authenticateUser(
        loginRequest
      );
      assertThat(response).isNotNull();
      assertThat(response.getStatusCodeValue()).isEqualTo(expectedStatus);

      JwtResponse jwtResponse = (JwtResponse) response.getBody();
      assertThat(jwtResponse).isNotNull();
      assertThat(jwtResponse.getToken()).isEqualTo(token);
      assertThat(jwtResponse.getAdmin()).isEqualTo(isAdmin);
    } else {
      when(
        authenticationManager.authenticate(
          any(UsernamePasswordAuthenticationToken.class)
        )
      )
        .thenThrow(new BadCredentialsException("Bad credentials"));
      when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

      assertThrows(
        BadCredentialsException.class,
        () -> {
          authController.authenticateUser(loginRequest);
        }
      );
    }
  }

  @ParameterizedTest(name = "({index}) : {0} [{7}]")
  @CsvSource(
    {
      "Success : new user registration, new@example.com, First, Last, password, false, encodedPassword, 200, 'User registered successfully!'",
      "Fail : email already exists, existing@example.com, First, Last, password, true, , 400, 'Error: Email is already taken!'",
    }
  )
  @DisplayName("Should handle Registration scenario ")
  void testRegisterUser(
    String scenarioName,
    String email,
    String firstName,
    String lastName,
    String password,
    boolean emailExists,
    String encodedPassword,
    int expectedStatus,
    String expectedMessage
  ) {
    SignupRequest signupRequest = new SignupRequest();
    signupRequest.setEmail(email);
    signupRequest.setFirstName(firstName);
    signupRequest.setLastName(lastName);
    signupRequest.setPassword(password);

    when(userRepository.existsByEmail(email)).thenReturn(emailExists);
    if (!emailExists) {
      when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
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
      assertThat(savedUser.getEmail()).isEqualTo(email);
      assertThat(savedUser.getFirstName()).isEqualTo(firstName);
      assertThat(savedUser.getLastName()).isEqualTo(lastName);
      assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
      assertThat(savedUser.isAdmin()).isFalse();
    }
  }
}
