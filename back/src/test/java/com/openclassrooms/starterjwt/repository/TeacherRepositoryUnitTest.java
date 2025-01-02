package com.openclassrooms.starterjwt.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.models.Teacher;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuiteDisplayName("REPOSITORY")
@DisplayName("Unit tests for TeacherRepository")
class TeacherRepositoryUnitTest {

  @Mock
  private TeacherRepository teacherRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @ParameterizedTest(name = "({index}) : {0} [{4}]")
  @CsvSource(
    {
      "Existing teacher id, 1, John, Doe, true",
      "Non-existing teacher id, 999, Jane, Smith, false",
    }
  )
  @DisplayName("Should find teacher by id ")
  void testFindById(
    String scenarioName,
    Long id,
    String firstName,
    String lastName,
    boolean shouldExist
  ) {
    Teacher teacher = new Teacher();
    teacher.setId(1L);
    teacher.setFirstName("John");
    teacher.setLastName("Doe");

    when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
    when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

    Optional<Teacher> result = teacherRepository.findById(id);

    if (shouldExist) {
      assertThat(result).isPresent();
      assertThat(result.get().getId()).isEqualTo(id);
      assertThat(result.get().getFirstName()).isEqualTo(firstName);
      assertThat(result.get().getLastName()).isEqualTo(lastName);
    } else {
      assertThat(result).isEmpty();
    }
    verify(teacherRepository, times(1)).findById(id);
  }

  @ParameterizedTest(name = "({index}) : {0} [{2}]")
  @CsvSource({ "Existing id, 1, true", "Unknown id, 999, false" })
  @DisplayName("Should check if teacher exists by id ")
  void testExistsById(String scenarioName, Long id, boolean shouldExist) {
    when(teacherRepository.existsById(1L)).thenReturn(true);
    when(teacherRepository.existsById(999L)).thenReturn(false);

    boolean exists = teacherRepository.existsById(id);

    assertThat(exists).isEqualTo(shouldExist);
    verify(teacherRepository, times(1)).existsById(id);
  }
}
