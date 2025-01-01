package com.openclassrooms.starterjwt.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TeacherServiceUnitTest {

  @Mock
  private TeacherRepository teacherRepository;

  @InjectMocks
  private TeacherService teacherService;

  public TeacherServiceUnitTest() {
    MockitoAnnotations.openMocks(this);
  }

  private static Teacher createTeacher(Long id, String firstName, String lastName) {
    return Teacher.builder()
        .id(id)
        .firstName(firstName)
        .lastName(lastName)
        .build();
  }

  @Test
  @DisplayName("Should return all teachers when findAll is called")
  void findAll_ShouldReturnAllTeachers() {
    List<Teacher> teachers = List.of(
        createTeacher(1L, "John", "Doe"),
        createTeacher(2L, "Jane", "Smith")
    );

    when(teacherRepository.findAll()).thenReturn(teachers);

    List<Teacher> result = teacherService.findAll();

    assertThat(result).isNotNull().hasSize(2).containsAll(teachers);
    verify(teacherRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("Should return a teacher by ID when it exists")
  void findById_ShouldReturnTeacher_WhenExists() {
    Teacher teacher = createTeacher(1L, "John", "Doe");

    when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

    Teacher result = teacherService.findById(1L);

    assertThat(result).isNotNull().isEqualTo(teacher);
    verify(teacherRepository, times(1)).findById(1L);
  }

  @Test
  @DisplayName("Should return null when teacher does not exist")
  void findById_ShouldReturnNull_WhenNotExists() {
    when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

    Teacher result = teacherService.findById(1L);

    assertThat(result).isNull();
    verify(teacherRepository, times(1)).findById(1L);
  }
}
