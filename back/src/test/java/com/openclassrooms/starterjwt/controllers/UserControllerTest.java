package com.openclassrooms.starterjwt.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

class UserControllerTest {
  @Mock
  private UserService userService;
  @Mock
  private UserMapper userMapper;
  @Mock
  private SecurityContext securityContext;
  @Mock
  private Authentication authentication;
  @InjectMocks
  private UserController userController;
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  @DisplayName("Should return status 200 and {userDto} in body when found by ID")
  void testFindById_Success() {
    User user = new User();
    user.setId(1L);
    UserDto userDto = new UserDto();
    userDto.setId(1L);

    when(userService.findById(1L)).thenReturn(user);
    when(userMapper.toDto(user)).thenReturn(userDto);

    ResponseEntity<?> response = userController.findById("1");

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(userDto, response.getBody());
  }

  @Test
  @DisplayName("Should return status 404 when user not found by ID")
  void testFindById_NotFound() {
    when(userService.findById(1L)).thenReturn(null);

    ResponseEntity<?> response = userController.findById("1");

    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should return status 400 for invalid ID format in findById")
  void testFindById_InvalidId() {
    
    ResponseEntity<?> response = userController.findById("&#@invalid");

    assertNotNull(response);
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should return status 200 and delete user #id when authorized")
  void testDelete_Success() {

    User user = new User();
    user.setId(666L);
    user.setEmail("delete-me@example.com");

    UserDetails userDetails = mock(UserDetails.class);

    when(userService.findById(666L)).thenReturn(user);
    when(userDetails.getUsername()).thenReturn("delete-me@example.com");

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userDetails);

    ResponseEntity<?> response = userController.save("666");

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should return 404 when trying to delete non-existing user #id")
  void testDelete_NotFound() {
    when(userService.findById(404L)).thenReturn(null);

    ResponseEntity<?> response = userController.save("404");

    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should return 400 for invalid ID format in delete")
  void testDelete_InvalidId() {
    
    ResponseEntity<?> response = userController.save("&@#invalid");

    assertNotNull(response);
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should return status 401 when trying to delete user without same email")
  void testDelete_Unauthorized() {
    User user = new User();
    user.setId(1L);
    user.setEmail("test@example.com");

    UserDetails userDetails = mock(UserDetails.class);

    when(userService.findById(1L)).thenReturn(user);
    when(userDetails.getUsername()).thenReturn("another_dude@example.com");

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userDetails);

    ResponseEntity<?> response = userController.save("1");

    assertNotNull(response);
    assertEquals(401, response.getStatusCodeValue());
  }
}
