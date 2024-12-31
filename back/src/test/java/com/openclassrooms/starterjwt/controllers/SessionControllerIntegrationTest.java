package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
public class SessionControllerIntegrationTest {

  @Autowired
  private SessionRepository sessionRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MockMvc mockMvc;

  @ParameterizedTest
  @CsvSource(
    {
      "1, Yoga Session, 200", // Successfully find session id#1, response="200-success"
      "999, , 404", // fail to find un-existing session id#999, response="404-Not Found"
      "invalid, , 400", // fail to find session with invalid id, response="400-Bad Request"
    }
  )
  @WithMockUser
  @DisplayName("Should handle findById scenarios and return appropriate status")
  void testFindById_Scenarios(
    String id,
    String expectedTitle,
    int expectedStatus
  ) throws Exception {
    // Prepare data in H2 database
    if ("1".equals(id)) {
      Session session = new Session();
      session.setId(1L);
      session.setName("Yoga Session");
      session.setDate(new Date());
      session.setDescription("A relaxing yoga session.");

      sessionRepository.save(session);
    }

    // Call the REST API
    MvcResult result = mockMvc
      .perform(
        MockMvcRequestBuilders
          .get("/api/session/" + id)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andReturn();

    // Verify the results
    int status = result.getResponse().getStatus();
    String content = result.getResponse().getContentAsString();

    // Assertions
    assertThat(status).isEqualTo(expectedStatus);
    if (expectedStatus == 200) {
      assertThat(content).contains(expectedTitle);
    } else {
      assertThat(content).doesNotContain("Yoga Session");
    }
  }

  @ParameterizedTest
  @CsvSource(
    {
      "session 1, my description,2012-01-01, 5, 200", // Regular case, Successfully create a session
      ", my description,2012-01-01, 5, 200", // Successfully create session without name
      "session 1, ,2012-01-01, 5, 200", // Successfully create session without description
      "session 1, my description, , 5, 400", // fail to create session, missing date
      "session 1, my description,2012-01-01, , 400", // fail to create session, missing teacher
    }
    // TO DO : creation success logic ?

  )
  @WithMockUser
  @DisplayName("Should handle create scenarios and return appropriate status")
  void testCreate_Scenarios(
    String name,
    String description,
    String date,
    String teacherId,
    int expectedStatus
  ) throws Exception {
    String payload = String.format(
      "{\"name\": \"%s\", \"description\": \"%s\", \"date\": \"%s\", \"teacher_id\": %s, \"users\": null}",
      name,
      description,
      date,
      teacherId
    );

    MvcResult result = mockMvc
      .perform(
        MockMvcRequestBuilders
          .post("/api/session")
          .contentType(MediaType.APPLICATION_JSON)
          .content(payload)
      )
      .andReturn();

    int status = result.getResponse().getStatus();

    assertThat(status).isEqualTo(expectedStatus);
  }

  @ParameterizedTest
  @CsvSource(
    {
      "1, session updated, updated descr, 5, 2012-01-01, 200", // Successfully update session id#1
      "1, , updated description, 5, 2012-01-01, 200", // Successfully update session without name
      "1, session updated, , 5, 2012-01-01, 200", // Successfully update session without description
      "1, session updated, updated descr, 5, , 400", // Fail to update session :missing date
      "1, session updated, updated descr, invalid, 2012-01-01, 400", // Fail to update session :Invalid teacher id
      //"999, session updated, updated descr, 5, 2012-01-01, 404", // Session not found
    }
    //TO DO : 404 logic ?
  )
  @WithMockUser
  @DisplayName("Should handle update scenarios and return appropriate status")
  void testUpdate_Scenarios(
    String id,
    String name,
    String description,
    String teacherId,
    String date,
    int expectedStatus
  ) throws Exception {
    // Prepare existing session in the database for update
    if ("1".equals(id)) {
      Session session = new Session();
      session.setId(1L);
      session.setName("Original Session");
      session.setDescription("Original Description");
      session.setDate(new Date());
      sessionRepository.save(session);
    }

    // Prepare JSON payload
    String payload = String.format(
      "{\"name\": \"%s\", \"description\": \"%s\", \"date\": \"%s\", \"teacher_id\": %s}",
      name,
      description,
      date,
      teacherId
    );

    // Call the REST API
    MvcResult result = mockMvc
      .perform(
        MockMvcRequestBuilders
          .put("/api/session/" + id)
          .contentType(MediaType.APPLICATION_JSON)
          .content(payload)
      )
      .andReturn();

    // Verify the results
    int status = result.getResponse().getStatus();
    String content = result.getResponse().getContentAsString();

    // Assertions
    assertThat(status).isEqualTo(expectedStatus);
    if (expectedStatus == 200) {
      assertThat(content).contains(date);
    }
  }

  @BeforeEach
  void setUp() {
    User existingUser = new User();
    existingUser.setEmail("existing_user@example.com");
    existingUser.setFirstName("Existing");
    existingUser.setLastName("User");
    userRepository.save(existingUser);

    Session session = new Session();
    session.setName("Session with Participants");
    session.setDescription("Session to test participation.");
    session.setDate(new Date());
    session.setUsers(List.of(existingUser));
    sessionRepository.save(session);

    User newUser = new User();
    newUser.setEmail("new_user@example.com");
    newUser.setFirstName("New");
    newUser.setLastName("User");
    userRepository.save(newUser);
  }

  @ParameterizedTest
  @CsvSource(
    {
      "1, 2, 200", // Successfully add user id #1 to session  n#1
      "1, invalid, 400", // Fail to participate :Invalid user ID format
      //"1, 1, 409", // Fail to participate :User already participating ->400?
      "999, 1, 404", // Fail to participate : Session not found
    }
    // TO DO : rejection logic ?
  )
  @WithMockUser
  @DisplayName(
    "Should handle participate scenarios and return appropriate status"
  )
  void testParticipate_Scenarios(
    String sessionId,
    String userId,
    int expectedStatus
  ) throws Exception {
    MvcResult result = mockMvc
      .perform(
        MockMvcRequestBuilders
          .post("/api/session/" + sessionId + "/participate/" + userId)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andReturn();

    int status = result.getResponse().getStatus();

    assertThat(status).isEqualTo(expectedStatus);
  }
}
