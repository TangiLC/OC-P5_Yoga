package com.openclassrooms.starterjwt.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.models.Teacher;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TeacherRepositoryUnitTest {

  @Mock
  private TeacherRepository teacherRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @ParameterizedTest
  @CsvSource(
    {
      "1, John, Doe, true", // Existing teacher
      "999, Jane, Smith, false", // Non-existing teacher
    }
  )
  @DisplayName("Should find teacher by ID")
  void testFindById(
    Long id,
    String firstName,
    String lastName,
    boolean shouldExist
  ) {
    // Prepare
    Teacher teacher = new Teacher();
    teacher.setId(1L);
    teacher.setFirstName("John");
    teacher.setLastName("Doe");

    // Mock repository behavior
    when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
    when(teacherRepository.findById(2L)).thenReturn(Optional.empty());

    // Execute
    Optional<Teacher> result = teacherRepository.findById(id);

    // Verify
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

  @ParameterizedTest
  @CsvSource(
    {
      "1, true", // ID exists
      "999, false", // ID doesn't exist
    }
  )
  @DisplayName("Should check if teacher exists by ID")
  void testExistsById(Long id, boolean shouldExist) {
    // Mock repository behavior
    when(teacherRepository.existsById(1L)).thenReturn(true);
    when(teacherRepository.existsById(999L)).thenReturn(false);

    // Execute
    boolean exists = teacherRepository.existsById(id);

    // Verify
    assertThat(exists).isEqualTo(shouldExist);
    verify(teacherRepository, times(1)).existsById(id);
  }
}
