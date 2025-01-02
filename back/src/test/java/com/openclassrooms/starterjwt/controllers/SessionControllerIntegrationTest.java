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
import org.junit.platform.suite.api.SuiteDisplayName;
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
@SuiteDisplayName("CONTROLLER")
@DisplayName("¤Integration tests for SessionController")
public class SessionControllerIntegrationTest {

  @Autowired
  private SessionRepository sessionRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MockMvc mockMvc;

  @ParameterizedTest(name = "({index}) : {0} [{3}]")
  @CsvSource(
    {
      "Successfully find session id#1, 1, Yoga Session, 200",
      "Fail to find un-existing session id#999, 999, , 404",
      "Fail to find session with invalid id, invalid, , 400",
    }
  )
  @DisplayName("Should handle findById scenario ")
  @WithMockUser
  void testFindById_Scenarios(
    String scenarioName,
    String id,
    String expectedTitle,
    int expectedStatus
  ) throws Exception {
    if ("1".equals(id)) {
      Session session = new Session();
      session.setId(1L);
      session.setName("Yoga Session");
      session.setDate(new Date());
      session.setDescription("A relaxing yoga session.");

      sessionRepository.save(session);
    }

    MvcResult result = mockMvc
      .perform(
        MockMvcRequestBuilders
          .get("/api/session/" + id)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andReturn();

    int status = result.getResponse().getStatus();
    String content = result.getResponse().getContentAsString();

    assertThat(status).isEqualTo(expectedStatus);
    if (expectedStatus == 200) {
      assertThat(content).contains(expectedTitle);
    } else {
      assertThat(content).doesNotContain("Yoga Session");
    }
  }

  @ParameterizedTest(name = "({index}) : {0} [{5}]")
  @CsvSource(
    {
      "Regular case : Successfully create a session, session 1, my description,2012-01-01, 5, 200",
      "Successfully create session without name, , my description,2012-01-01, 5, 200",
      "Successfully create session without description, session 1, ,2012-01-01, 5, 200",
      "Fail to create session : missing date, session 1, my description, , 5, 400",
      "Fail to create session : missing teacher, session 1, my description,2012-01-01, , 400",
    }
    // TO DO : creation success logic ?

  )
  @DisplayName("Should handle Create scenario ")
  @WithMockUser
  void testCreate_Scenarios(
    String scenarioName,
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

  @ParameterizedTest(name = "({index}) : {0} [{6}]")
  @CsvSource(
    {
      "Successfully update session id#1, 1, session updated, updated descr, 5, 2012-01-01, 200",
      "Successfully update session without name, 1, , updated description, 5, 2012-01-01, 200",
      "Successfully update session without description, 1, session updated, , 5, 2012-01-01, 200",
      "Fail to update session :missing date, 1, session updated, updated descr, 5, , 400",
      "Fail to update session :Invalid teacher id, 1, session updated, updated descr, invalid, 2012-01-01, 400",
      //"Session not found, 999, session updated, updated descr, 5, 2012-01-01, 404", //->200 ?
    }
    //TO DO : 404 logic ?
  )
  @DisplayName("Should handle Update scenario ")
  @WithMockUser
  void testUpdate_Scenarios(
    String scenarioName,
    String id,
    String name,
    String description,
    String teacherId,
    String date,
    int expectedStatus
  ) throws Exception {
    if ("1".equals(id)) {
      Session session = new Session();
      session.setId(1L);
      session.setName("Original Session");
      session.setDescription("Original Description");
      session.setDate(new Date());
      sessionRepository.save(session);
    }

    String payload = String.format(
      "{\"name\": \"%s\", \"description\": \"%s\", \"date\": \"%s\", \"teacher_id\": %s}",
      name,
      description,
      date,
      teacherId
    );

    MvcResult result = mockMvc
      .perform(
        MockMvcRequestBuilders
          .put("/api/session/" + id)
          .contentType(MediaType.APPLICATION_JSON)
          .content(payload)
      )
      .andReturn();

    int status = result.getResponse().getStatus();
    String content = result.getResponse().getContentAsString();

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
    session.setUsers(new ArrayList<>(List.of(existingUser)));
    sessionRepository.save(session);

    User newUser = new User();
    newUser.setEmail("new_user@example.com");
    newUser.setFirstName("New");
    newUser.setLastName("User");
    userRepository.save(newUser);
  }

  @ParameterizedTest(name = "({index}) : {0} [{3}]")
  @CsvSource(
    {
      "Successfully add user id #1 to session n#1, 1, 2, 200", //
      "Fail to participate :Invalid user ID format, 1, invalid, 400", //
      //"Fail to participate :User already participating, 1, 1, 409", //  ->400?
      "Fail to participate : Session not found, 999, 1, 404", //
    }
    // TO DO : rejection logic 400/409?
  )
  @DisplayName("Should handle Participate scenarios ")
  @WithMockUser
  void testParticipate_Scenarios(
    String scenarioName,
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

  @ParameterizedTest(name = "({index}) : {0} [{3}]")
  @CsvSource(
    {
      "Successfully remove user #id from session #id, 1, 1, 200",
      //"Fail to remove : User not participating in session, 1, 2, 404", // ->400
      "Fail to remove : Invalid user ID format, 1, invalid, 400",
      "Fail to remove : Session not found, 999, 1, 404",
    }
    //TO DO : rejection logic 404/400?
  )
  @DisplayName("Should handle noLongerParticipate scenario ")
  @WithMockUser
  void testNoLongerParticipate_Scenarios(
    String scenarioName,
    String sessionId,
    String userId,
    int expectedStatus
  ) throws Exception {
    MvcResult result = mockMvc
      .perform(
        MockMvcRequestBuilders
          .delete("/api/session/" + sessionId + "/participate/" + userId)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andReturn();

    int status = result.getResponse().getStatus();

    assertThat(status).isEqualTo(expectedStatus);
  }
}
