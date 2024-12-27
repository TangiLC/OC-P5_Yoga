package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
  @MethodSource("findByIdScenarios")
  @DisplayName("Should handle different findById scenarios")
  void testFindByIdScenarios(
    String scenarioName,
    String inputId,
    Teacher serviceResponse,
    TeacherDto mappedResponse,
    int expectedStatus
  ) {
    if (!"invalid".equals(inputId)) {
      // Mock behavior only for valid IDs
      if (serviceResponse != null) {
        when(teacherService.findById(Long.parseLong(inputId)))
          .thenReturn(serviceResponse);
        when(teacherMapper.toDto(serviceResponse)).thenReturn(mappedResponse);
      } else {
        when(teacherService.findById(Long.parseLong(inputId))).thenReturn(null);
      }
    }

    // Act
    ResponseEntity<?> response = teacherController.findById(inputId);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getStatusCodeValue()).isEqualTo(expectedStatus);
    if (expectedStatus == 200) {
      assertThat(response.getBody()).isEqualTo(mappedResponse);
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("findAllScenarios")
  @DisplayName("Should handle findAll scenarios")
  void testFindAllScenarios(
    String scenarioName,
    List<Teacher> serviceResponse,
    List<TeacherDto> mappedResponse,
    int expectedStatus
  ) {
    when(teacherService.findAll()).thenReturn(serviceResponse);
    when(teacherMapper.toDto(serviceResponse)).thenReturn(mappedResponse);

    // Act
    ResponseEntity<?> response = teacherController.findAll();

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getStatusCodeValue()).isEqualTo(expectedStatus);
    if (expectedStatus == 200) {
      assertThat(response.getBody()).isEqualTo(mappedResponse);
    }
  }

  // Test scenarios for findById
  private static Stream<Arguments> findByIdScenarios() {
    Teacher teacher = new Teacher();
    teacher.setId(1L);
    TeacherDto teacherDto = new TeacherDto();
    teacherDto.setId(1L);

    return Stream.of(
      Arguments.of(
        "Should return 200 and teacherDto for valid ID",
        "1",
        teacher,
        teacherDto,
        200
      ),
      Arguments.of(
        "Should return 404 for non-existing ID",
        "999",
        null,
        null,
        404
      ),
      Arguments.of(
        "Should return 400 for invalid ID format",
        "invalid",
        null,
        null,
        400
      )
    );
  }

  // Test scenarios for findAll
  private static Stream<Arguments> findAllScenarios() {
    List<Teacher> teachers = List.of(new Teacher(), new Teacher());
    List<TeacherDto> teacherDtos = List.of(new TeacherDto(), new TeacherDto());

    return Stream.of(
      Arguments.of(
        "Should return 200 and list of teacherDtos",
        teachers,
        teacherDtos,
        200
      ),
      Arguments.of(
        "Should return 200 and empty list if no teachers found",
        List.of(),
        List.of(),
        200
      )
    );
  }
}
