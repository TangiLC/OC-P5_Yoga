package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;

import org.junit.platform.suite.api.SuiteDisplayName;

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
@SuiteDisplayName("CONTROLLER")
@DisplayName("¤Integration tests for TeacherController")
public class TeacherControllerIntegrationTest {

  @Autowired
  private TeacherRepository teacherRepository;

  @Autowired
  private MockMvc mockMvc;

  @ParameterizedTest(name = "({index}) : {0} [200]")
  @CsvSource({ "List is not empty, 3, true", "List is empty, 0, false" })
  @WithMockUser
  @DisplayName("Should return List of all teachers ")
  void testFindAll_Teachers(
    String scenarioName,
    int numberOfTeachers,
    boolean shouldContainTeachers
  ) throws Exception {
    if (numberOfTeachers > 0) {
      Teacher teacher1 = new Teacher();
      teacher1.setFirstName("Margot").setLastName("Delahaye").setId(1L);
      teacherRepository.save(teacher1);
      Teacher teacher2 = new Teacher();
      teacher2.setFirstName("Helene").setLastName("Thiercelin").setId(2L);
      teacherRepository.save(teacher2);
      Teacher teacher3 = new Teacher();
      teacher3.setFirstName("John").setLastName("Doe").setId(3L);
      teacherRepository.save(teacher3);
    }

    MvcResult result = mockMvc
      .perform(
        MockMvcRequestBuilders
          .get("/api/teacher")
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andReturn();

    int status = result.getResponse().getStatus();
    String content = result.getResponse().getContentAsString();

    assertThat(status).isEqualTo(200);
    if (shouldContainTeachers) {
      assertThat(content)
        .contains("Margot", "Delahaye", "Helene", "Thiercelin", "John", "Doe");
    } else {
      assertThat(content).isEqualTo("[]");
    }
  }

  @ParameterizedTest(name = "({index}) : {0} [{4}]")
  @CsvSource(
    {
      "Regular case : successfully Find teacher #1, 1, Margot, Delahaye, 200",
      "Regular case : successfully Find teacher #2, 2, Helene, Thiercelin, 200",
      "Fail to find unknown id teacher, 999, , , 404",
      "Fail to find invalid id teacher, invalid, , , 400",
    }
  )
  @WithMockUser
  @DisplayName("Should handle FindById scenario ")
  void testFindById_Scenarios(
    String scenarioName,
    String id,
    String expectedFirstName,
    String expectedLastName,
    int expectedStatus
  ) throws Exception {

    Teacher teacher1 = new Teacher();
    teacher1.setId(1L).setFirstName("Margot").setLastName("Delahaye");
    teacherRepository.save(teacher1);
    Teacher teacher2 = new Teacher();
    teacher2.setId(2L).setFirstName("Helene").setLastName("Thiercelin");
    teacherRepository.save(teacher2);

    MvcResult result = mockMvc
      .perform(
        MockMvcRequestBuilders
          .get("/api/teacher/" + id)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andReturn();

    int status = result.getResponse().getStatus();
    String content = result.getResponse().getContentAsString();

    assertThat(status).isEqualTo(expectedStatus);
    if (expectedStatus == 200) {
      assertThat(content).contains(expectedFirstName, expectedLastName);
    } else {
      assertThat(content)
        .doesNotContain("Margot", "Delahaye", "Helene", "Thiercelin");
    }
  }
}
