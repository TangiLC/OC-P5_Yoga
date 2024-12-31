package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
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
public class TeacherControllerIntegrationTest {

  @Autowired
  private TeacherRepository teacherRepository;

  @Autowired
  private MockMvc mockMvc;

  @ParameterizedTest
  @CsvSource(
    {
      "3, true", // 3 teachers exist, status=200, response contains all 3 teachers
      "0, false", // No teachers exist, status=200, empty response
    }
  )
  @WithMockUser
  @DisplayName("Should return all teachers or empty list")
  void testFindAll_Teachers(
    int numberOfTeachers,
    boolean shouldContainTeachers
  ) throws Exception {
    // Préparer les données dans H2
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

    // Appeler l'API REST
    MvcResult result = mockMvc
      .perform(
        MockMvcRequestBuilders
          .get("/api/teacher")
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andReturn();

    // Vérifier les résultats
    int status = result.getResponse().getStatus();
    String content = result.getResponse().getContentAsString();

    // Assertions
    assertThat(status).isEqualTo(200);
    if (shouldContainTeachers) {
      assertThat(content)
        .contains("Margot", "Delahaye", "Helene", "Thiercelin", "John", "Doe");
    } else {
      assertThat(content).isEqualTo("[]");
    }
  }

  @ParameterizedTest
  @CsvSource(
    {
      "1, Margot, Delahaye, 200", // Find teacher #1, response="200-success"
      "2, Helene, Thiercelin, 200", // Find teacher #2, response="200-success"
      "999, , , 404", // fail to find not-existing Teacher #999, response="404-not found"
      "invalid, , , 400", // Invalid ID format, response="400-Bad Request"
    }
  )
  @WithMockUser
  @DisplayName("Should handle findById scenarios and return appropriate status")
  void testFindById_Scenarios(
    String id,
    String expectedFirstName,
    String expectedLastName,
    int expectedStatus
  ) throws Exception {
    // Préparer les données dans H2

    Teacher teacher1 = new Teacher();
    teacher1.setId(1L).setFirstName("Margot").setLastName("Delahaye");
    teacherRepository.save(teacher1);
    Teacher teacher2 = new Teacher();
    teacher2.setId(2L).setFirstName("Helene").setLastName("Thiercelin");
    teacherRepository.save(teacher2);

    // Appeler l'API REST
    MvcResult result = mockMvc
      .perform(
        MockMvcRequestBuilders
          .get("/api/teacher/" + id)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andReturn();

    // Vérifier les résultats
    int status = result.getResponse().getStatus();
    String content = result.getResponse().getContentAsString();

    // Assertions
    assertThat(status).isEqualTo(expectedStatus);
    if (expectedStatus == 200) {
      assertThat(content).contains(expectedFirstName, expectedLastName);
    } else {
      assertThat(content)
        .doesNotContain("Margot", "Delahaye", "Helene", "Thiercelin");
    }
  }
}