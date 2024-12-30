package com.openclassrooms.starterjwt.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

class TeacherMapperTest {

  private final TeacherMapper teacherMapper = Mappers.getMapper(
    TeacherMapper.class
  );

  private static Teacher createTeacher(
    Long id,
    String firstName,
    String lastName
  ) {
    Teacher teacher = new Teacher();
    teacher.setId(id);
    teacher.setFirstName(firstName);
    teacher.setLastName(lastName);
    teacher.setCreatedAt(null);
    teacher.setUpdatedAt(null);
    return teacher;
  }

  private static TeacherDto createTeacherDto(
    Long id,
    String firstName,
    String lastName
  ) {
    TeacherDto teacherDto = new TeacherDto();
    teacherDto.setId(id);
    teacherDto.setFirstName(firstName);
    teacherDto.setLastName(lastName);
    return teacherDto;
  }

  private static Stream<Arguments> toDtoTestCases() {
    return Stream.of(
      // regular case
      Arguments.of(
        createTeacher(1L, "Teacher", "Mock1"),
        1L,
        "Teacher",
        "Mock1"
      ),
      // without first name
      Arguments.of(createTeacher(3L, null, "Mock3"), 3L, null, "Mock3"),
      // without last name
      Arguments.of(createTeacher(4L, "Teacher", null), 4L, "Teacher", null),
      // without name
      Arguments.of(createTeacher(5L, null, null), 5L, null, null),
      // name containing spaces
      Arguments.of(
        createTeacher(6L, "First Name", "Last Name"),
        6L,
        "First Name",
        "Last Name"
      ),
      // name containing special char
      Arguments.of(
        createTeacher(7L, "Téà-cher", "Mòck"),
        7L,
        "Téà-cher",
        "Mòck"
      )
    );
  }

  private static Stream<Arguments> toEntityTestCases() {
    return Stream.of(
      Arguments.of(
        createTeacherDto(1L, "Teacher", "Mock1"),
        1L,
        "Teacher",
        "Mock1"
      ),
      Arguments.of(createTeacherDto(3L, null, "Mock3"), 3L, null, "Mock3"),
      Arguments.of(createTeacherDto(4L, "Teacher", null), 4L, "Teacher", null),
      Arguments.of(createTeacherDto(5L, null, null), 5L, null, null),
      Arguments.of(
        createTeacherDto(6L, "First Name", "Last Name"),
        6L,
        "First Name",
        "Last Name"
      ),
      Arguments.of(
        createTeacherDto(7L, "Téà-cher", "Mòck"),
        7L,
        "Téà-cher",
        "Mòck"
      )
    );
  }

  @ParameterizedTest
  @MethodSource("toDtoTestCases")
  void toDto_ShouldMapCorrectly(
    Teacher teacher,
    Long expectedId,
    String expectedFirstName,
    String expectedLastName
  ) {
    TeacherDto result = teacherMapper.toDto(teacher);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(expectedId);
    assertThat(result.getFirstName()).isEqualTo(expectedFirstName);
    assertThat(result.getLastName()).isEqualTo(expectedLastName);
  }

  @ParameterizedTest
  @MethodSource("toEntityTestCases")
  void toEntity_ShouldMapCorrectly(
    TeacherDto teacherDto,
    Long expectedId,
    String expectedFirstName,
    String expectedLastName
  ) {
    Teacher result = teacherMapper.toEntity(teacherDto);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(expectedId);
    assertThat(result.getFirstName()).isEqualTo(expectedFirstName);
    assertThat(result.getLastName()).isEqualTo(expectedLastName);
  }
}
