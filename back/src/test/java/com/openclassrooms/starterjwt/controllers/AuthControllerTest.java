package com.openclassrooms.starterjwt.controllers;

import static org.junit.jupiter.api.Assertions.*;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthControllerTest {

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

  @Test
  @DisplayName("Should return 200 and token when successful login")
  void testAuthenticateUser_Success() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("test@example.com");
    loginRequest.setPassword("password");

    Authentication authentication = mock(Authentication.class);

    UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
    when(userDetails.getId()).thenReturn(1L);
    when(userDetails.getUsername()).thenReturn("test@example.com");
    when(userDetails.getFirstName()).thenReturn("First");
    when(userDetails.getLastName()).thenReturn("Last");
    when(userDetails.getPassword()).thenReturn("password");
    when(userDetails.getAdmin()).thenReturn(true);

    when(
      authenticationManager.authenticate(
        any(UsernamePasswordAuthenticationToken.class)
      )
    )
      .thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(jwtUtils.generateJwtToken(authentication))
      .thenReturn("mockedJwtToken");
    when(userRepository.findByEmail("test@example.com"))
      .thenReturn(
        Optional.of(
          new User("test@example.com", "Last", "First", "password", true)
        )
      );

    ResponseEntity<?> response = authController.authenticateUser(loginRequest);

    assertNotNull(response); //should return a response, status 200
    assertEquals(200, response.getStatusCodeValue());
    JwtResponse jwtResponse = (JwtResponse) response.getBody();
    assertNotNull(jwtResponse); //should include jwt token in body
    assertEquals("mockedJwtToken", jwtResponse.getToken());
    assertTrue(jwtResponse.getAdmin()); //should include boolean admin in body
  }

  @Test
  @DisplayName(
    "Should return 400 error on register attempt if user already exists"
  )
  void testRegisterUser_EmailAlreadyExists() {
    SignupRequest signupRequest = new SignupRequest();
    signupRequest.setEmail("existing@example.com");
    signupRequest.setFirstName("First");
    signupRequest.setLastName("Last");
    signupRequest.setPassword("password");

    when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

    ResponseEntity<?> response = authController.registerUser(signupRequest);

    assertNotNull(response); //should return a response, status 400
    assertEquals(400, response.getStatusCodeValue());
    MessageResponse messageResponse = (MessageResponse) response.getBody();
    assertNotNull(messageResponse); //should include error message in body
    assertEquals(
      "Error: Email is already taken!",
      messageResponse.getMessage()
    );
  }

  @Test
  @DisplayName("Should return 200 and token when successful new register")
  void testRegisterUser_Success() {
    SignupRequest signupRequest = new SignupRequest();
    signupRequest.setEmail("new@example.com");
    signupRequest.setFirstName("First");
    signupRequest.setLastName("Last");
    signupRequest.setPassword("password");

    when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
    when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

    ResponseEntity<?> response = authController.registerUser(signupRequest);

    assertNotNull(response); //should return a response, status 200
    assertEquals(200, response.getStatusCodeValue());
    MessageResponse messageResponse = (MessageResponse) response.getBody();
    assertNotNull(messageResponse); //should return success message in body
    assertEquals("User registered successfully!", messageResponse.getMessage());

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    assertNotNull(savedUser); //should store user data in DB
    assertEquals("new@example.com", savedUser.getEmail());
    assertEquals("First", savedUser.getFirstName());
    assertEquals("Last", savedUser.getLastName());
    assertEquals("encodedPassword", savedUser.getPassword());
    assertFalse(savedUser.isAdmin());
  }

  @Test
  @DisplayName("Should handle the user not found case")
  void testAuthenticateUser_UserNotFound() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("unknown@example.com");
    loginRequest.setPassword("wrongpassword");

    Authentication authentication = mock(Authentication.class);
    when(
      authenticationManager.authenticate(
        any(UsernamePasswordAuthenticationToken.class)
      )
    )
      .thenReturn(authentication);

    UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
    when(userDetails.getUsername()).thenReturn("unknown@example.com");
    when(authentication.getPrincipal()).thenReturn(userDetails);

    when(userRepository.findByEmail("unknown@example.com"))
      .thenReturn(Optional.empty());

    ResponseEntity<?> response = authController.authenticateUser(loginRequest);

    /*WARN : Actual code do not properly handle null user
     *   response leads to 504 gateway timeout,
     *   maybe a 404 response would be more appropriate
     */

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    JwtResponse jwtResponse = (JwtResponse) response.getBody();
    assertNotNull(jwtResponse);
    assertFalse(jwtResponse.getAdmin());
  }
}
