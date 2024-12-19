package com.openclassrooms.starterjwt.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class TeacherControllerTest {

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

  @Test
  @DisplayName("Should return status 200 and teacherDto in body when successfully found by ID")
  void testFindById_Success() {
    // Mock data
    Teacher teacher = new Teacher();
    teacher.setId(1L);
    TeacherDto teacherDto = new TeacherDto(); 
    teacherDto.setId(1L);


    when(teacherService.findById(1L)).thenReturn(teacher);
    when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

    // Call the method
    ResponseEntity<?> response = teacherController.findById("1");

    // Assertions
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(teacherDto, response.getBody());
  }

  @Test
  @DisplayName("Should return status 404 when teacher not found by ID")
  void testFindById_NotFound() {
    when(teacherService.findById(1L)).thenReturn(null);

    ResponseEntity<?> response = teacherController.findById("1");

    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should return status 400 for invalid ID format in findById")
  void testFindById_InvalidId() {
    ResponseEntity<?> response = teacherController.findById("&@#invalid");

    assertNotNull(response);
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should return response 200 and [all teachers dto] in body")
  void testFindAll() {
    List<Teacher> teachers = Arrays.asList(new Teacher(), new Teacher());
    List<TeacherDto> teacherDtos = Arrays.asList(
      new TeacherDto(),
      new TeacherDto()
    ); 
    when(teacherService.findAll()).thenReturn(teachers);
    when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

    ResponseEntity<?> response = teacherController.findAll();

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(teacherDtos, response.getBody());
  }
}
