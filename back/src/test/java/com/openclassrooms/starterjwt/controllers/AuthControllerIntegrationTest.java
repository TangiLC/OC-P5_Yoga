package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SuiteDisplayName("CONTROLLER")
@DisplayName("¤Integration tests for AuthController")
public class AuthControllerIntegrationTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @ParameterizedTest(name = "({index}) : {0} [{4}]")
  @CsvSource(
    {
      "Regular case : successfully login for Valid admin, yoga@studio.com, password, true, 200",
      "Regular case : successfully login for Valid user, user@studio.com, password, false, 200",
      "Fail to login : Wrong password,yoga@studio.com, wrongpassword, true, 401",
      "Fail to login : Unknown user,unknown@studio.com, password, false, 401",
      "Bad Request : empty password,yoga@studio.com, '', false, 400",
      //"Bad Request : Invalid email format,invalid-email, password, false, 400", //  ->401
    }
  ) // TO DO : handle email format ?
  @DisplayName("Should handle Login scenario ")
  void testAuthenticate_Scenarios(
    String scenarioName,
    String email,
    String password,
    boolean isAdmin,
    int expectedStatus
  ) throws Exception {
    if (email.equals("yoga@studio.com") || email.equals("user@studio.com")) {
      User user = new User();
      user
        .setEmail(email)
        .setPassword(passwordEncoder.encode("password")) // Encode the password properly
        .setFirstName("John")
        .setLastName("Doe")
        .setAdmin(isAdmin);
      userRepository.save(user);
    }

    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail(email);
    loginRequest.setPassword(password);

    MvcResult result = mockMvc
      .perform(
        MockMvcRequestBuilders
          .post("/api/auth/login")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(loginRequest))
      )
      .andReturn();

    int status = result.getResponse().getStatus();
    String content = result.getResponse().getContentAsString();

    assertThat(status).isEqualTo(expectedStatus);
    if (expectedStatus == 200) {
      assertThat(content).contains("token");
      assertThat(content).contains(email);
      assertThat(content).contains(String.valueOf(isAdmin));
    }
  }

  @ParameterizedTest(name = "({index}) : {0} [{5}]")
  @CsvSource(
    {
      "Regular case : successfully register, new@studio.com, John, Doe, password, 200, User registered successfully!",
      "Fail to register :Email already exists, existing@studio.com, John, Doe, password, 400, Error: Email is already taken!",
      "Fail to register : Invalid email format, invalid-email, John, Doe, password, 400, ",
      "Fail to register : Missing firstName, new@studio.com, , Doe, password, 400, ",
      "Fail to register : Missing lastName, new@studio.com, John, , password, 400, ",
      "Fail to register : Missing lastName, new@studio.com, John, Doe, , 400, ",
    }
  )
  @DisplayName("Should handle Registration scenario ")
  void testRegister_Scenarios(
    String scenarioName,
    String email,
    String firstName,
    String lastName,
    String password,
    int expectedStatus,
    String expectedMessage
  ) throws Exception {
    if (email.equals("existing@studio.com")) {
      User existingUser = new User();
      existingUser
        .setEmail(email)
        .setPassword(passwordEncoder.encode(password))
        .setFirstName("John")
        .setLastName("Doe")
        .setAdmin(false);
      userRepository.save(existingUser);
    }

    SignupRequest signupRequest = new SignupRequest();
    signupRequest.setEmail(email);
    signupRequest.setFirstName(firstName);
    signupRequest.setLastName(lastName);
    signupRequest.setPassword(password);

    MvcResult result = mockMvc
      .perform(
        MockMvcRequestBuilders
          .post("/api/auth/register")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(signupRequest))
      )
      .andReturn();

    int status = result.getResponse().getStatus();
    String content = result.getResponse().getContentAsString();

    assertThat(status).isEqualTo(expectedStatus);
    if (expectedMessage != null && !expectedMessage.isEmpty()) {
      assertThat(content).contains(expectedMessage);
    }

    if (expectedStatus == 200) {
      User savedUser = userRepository.findByEmail(email).orElse(null);
      assertThat(savedUser).isNotNull();
      assertThat(savedUser.getEmail()).isEqualTo(email);
      assertThat(savedUser.getFirstName()).isEqualTo(firstName);
      assertThat(savedUser.getLastName()).isEqualTo(lastName);
      assertThat(savedUser.isAdmin()).isFalse();
    }
  }
}
