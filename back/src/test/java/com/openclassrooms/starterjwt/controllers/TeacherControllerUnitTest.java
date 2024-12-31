package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class TeacherControllerUnitTest {

  @Mock
  private TeacherService teacherService;

  @Mock
  private TeacherMapper teacherMapper;

  @InjectMocks
  private TeacherController teacherController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
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
    Teacher serviceResponse = serviceResponseExists ? new Teacher() : null;
    TeacherDto mappedResponse = serviceResponseExists ? new TeacherDto() : null;

    if (!"invalid".equals(inputId)) {
      when(
        teacherService.findById(
          serviceResponseExists ? Long.parseLong(inputId) : null
        )
      )
        .thenReturn(serviceResponse);
      if (serviceResponseExists) {
        when(teacherMapper.toDto(serviceResponse)).thenReturn(mappedResponse);
      }
    }

    ResponseEntity<?> response = teacherController.findById(inputId);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCodeValue()).isEqualTo(expectedStatus);
    if (expectedStatus == 200) {
      assertThat(response.getBody()).isEqualTo(mappedResponse);
    }
  }

  @ParameterizedTest(name = "{0}")
  @CsvSource({ "With Teachers, true, 200", "No Teachers, false, 200" })
  @DisplayName("Should handle findAll scenarios")
  void testFindAllScenarios(
    String scenarioName,
    boolean hasTeachers,
    int expectedStatus
  ) {
    List<Teacher> serviceResponse = hasTeachers
      ? List.of(new Teacher(), new Teacher())
      : List.of();
    List<TeacherDto> mappedResponse = hasTeachers
      ? List.of(new TeacherDto(), new TeacherDto())
      : List.of();

    when(teacherService.findAll()).thenReturn(serviceResponse);
    when(teacherMapper.toDto(serviceResponse)).thenReturn(mappedResponse);

    ResponseEntity<?> response = teacherController.findAll();

    assertThat(response).isNotNull();
    assertThat(response.getStatusCodeValue()).isEqualTo(expectedStatus);
    if (expectedStatus == 200) {
      assertThat(response.getBody()).isEqualTo(mappedResponse);
    }
  }
}
