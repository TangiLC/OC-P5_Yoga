package com.openclassrooms.starterjwt.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.mapstruct.factory.Mappers;

@SuiteDisplayName("MAPPER")
@DisplayName("Unit tests for TeachersMapper")
class TeachersMapperUnitTest {

  private final TeacherMapper teacherMapper = Mappers.getMapper(
    TeacherMapper.class
  );

  private static List<Teacher> createTeacherList() {
    return List.of(
      new Teacher(1L, "Mock1", "Teacher", null, null),
      new Teacher(2L, "Mock2", null, null, null),
      new Teacher(3L, null, "Teacher", null, null),
      new Teacher(4L, null, null, null, null),
      new Teacher(5L, "Mòck", "Téà-cher", null, null)
    );
  }

  private static List<TeacherDto> createTeacherDtoList() {
    TeacherDto dto1 = new TeacherDto();
    dto1.setId(1L);
    dto1.setFirstName("Teacher");
    dto1.setLastName("Mock1");

    TeacherDto dto2 = new TeacherDto();
    dto2.setId(2L);
    dto2.setFirstName(null);
    dto2.setLastName("Mock2");

    TeacherDto dto3 = new TeacherDto();
    dto3.setId(3L);
    dto3.setFirstName("Teacher");
    dto3.setLastName(null);

    TeacherDto dto4 = new TeacherDto();
    dto4.setId(4L);
    dto4.setFirstName(null);
    dto4.setLastName(null);

    TeacherDto dto5 = new TeacherDto();
    dto5.setId(5L);
    dto5.setFirstName("Téà-cher");
    dto5.setLastName("Mòck");

    return List.of(dto1, dto2, dto3, dto4, dto5);
  }

  private static Stream<Arguments> toDtoListTestCases() {
    return Stream.of(Arguments.of(createTeacherList(), createTeacherDtoList()));
  }

  private static Stream<Arguments> toEntityListTestCases() {
    return Stream.of(Arguments.of(createTeacherDtoList(), createTeacherList()));
  }

  @ParameterizedTest
  @MethodSource("toDtoListTestCases")
  @DisplayName("Should map correctly toDto List")
  void toDtoList_ShouldMapCorrectly(
    List<Teacher> teachers,
    List<TeacherDto> expectedTeacherDtos
  ) {
    List<TeacherDto> result = teacherMapper.toDto(teachers);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(expectedTeacherDtos.size());

    for (int i = 0; i < expectedTeacherDtos.size(); i++) {
      TeacherDto expected = expectedTeacherDtos.get(i);
      TeacherDto actual = result.get(i);

      assertThat(actual.getId()).isEqualTo(expected.getId());
      assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
      assertThat(actual.getLastName()).isEqualTo(expected.getLastName());
    }
  }

  @ParameterizedTest
  @MethodSource("toEntityListTestCases")
  @DisplayName("Should map correctly toEntity List")
  void toEntityList_ShouldMapCorrectly(
    List<TeacherDto> teacherDtos,
    List<Teacher> expectedTeachers
  ) {
    List<Teacher> result = teacherMapper.toEntity(teacherDtos);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(expectedTeachers.size());

    for (int i = 0; i < expectedTeachers.size(); i++) {
      Teacher expected = expectedTeachers.get(i);
      Teacher actual = result.get(i);

      assertThat(actual.getId()).isEqualTo(expected.getId());
      assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
      assertThat(actual.getLastName()).isEqualTo(expected.getLastName());
    }
  }
}
