package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;

import org.junit.platform.suite.api.SuiteDisplayName;

import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
@DisplayName("¤Integration tests for UserController")
public class UserControllerIntegrationTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MockMvc mockMvc;

  @ParameterizedTest(name = "({index}) : {0} [{2}]")
  @CsvSource(
    {
      "Regular case : successfully find user,1, 200, yoga@studio.com",
      "Fail to find : user id unknown, 999, 404, ",
      "Fail to find : user id invalid, invalid, 400, ",
    }
  )
  @WithMockUser
  @DisplayName("Should handle different FindById scenario ")
  void testFindById_scenarios(
    String scenarioName,
    String id,
    int expectedStatus,
    String expectedEmail
  ) throws Exception {
    if ("1".equals(id)) {
      User user = new User();
      user.setEmail("yoga@studio.com");
      user.setLastName("Admin");
      user.setFirstName("Admin");
      user.setPassword(
        "$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq"
      );
      user.setAdmin(true);
      userRepository.save(user); //save in H2 before test
    }

    MvcResult result = mockMvc
      .perform(
        MockMvcRequestBuilders
          .get("/api/user/" + id)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andReturn();

    int status = result.getResponse().getStatus();
    String content = result.getResponse().getContentAsString();

    assertThat(status).isEqualTo(expectedStatus);
    if (expectedEmail != null) {
      assertThat(content).contains(expectedEmail);
    }
  }

  @ParameterizedTest(name = "({index}) : {0} [{4}]")
  @CsvSource(
    {
      "Regular case : successfully delete user, 1, yoga@studio.com, yoga@studio.com, 200",
      "Fail to delete : unauth user, 1, yoga@studio.com, other@studio.com, 401",
      "Fail to delete : user id not found, 999, yoga@studio.com, yoga@studio.com, 404",
      "Fail to delete : user id invalid, invalid, yoga@studio.com, yoga@studio.com, 400",
    }
  )
  @WithMockUser
  void testDelete(
    String scenarioName,
    String id,
    String userEmail,
    String loggedInUserEmail,
    int expectedStatus
  ) throws Exception {
    if ("1".equals(id)) {
      User user = new User();
      user.setEmail(userEmail);
      user.setLastName("Admin");
      user.setFirstName("Admin");
      user.setPassword(
        "$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq"
      );
      user.setAdmin(true);
      userRepository.save(user); // save in H2 before test
    }

    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
      loggedInUserEmail,
      "",
      Collections.emptyList()
    );
    SecurityContextHolder
      .getContext()
      .setAuthentication(
        new UsernamePasswordAuthenticationToken(
          userDetails,
          null,
          userDetails.getAuthorities()
        )
      );

    MvcResult result = mockMvc
      .perform(
        MockMvcRequestBuilders
          .delete("/api/user/" + id)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andReturn();

    int status = result.getResponse().getStatus();

    assertThat(status).isEqualTo(expectedStatus);
    if (expectedStatus == 200) {
      assertThat(userService.findById(Long.parseLong(id))).isNull();
    } else if (expectedStatus == 401) {
      assertThat(userService.findById(Long.parseLong(id))).isNotNull();
    }
  }
}
