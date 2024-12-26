package com.openclassrooms.starterjwt.security.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class UserDetailsServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserDetailsServiceImpl userDetailsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("<UT>Should return UserDetails when user is found")
  void testLoadUserByUsername_UserFound() {
    String email = "test@example.com";
    User mockUser = new User();
    mockUser.setId(1L);
    mockUser.setEmail(email);
    mockUser.setFirstName("John");
    mockUser.setLastName("Doe");
    mockUser.setPassword("password");

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

    assertNotNull(userDetails);
    assertEquals(email, userDetails.getUsername());
    assertEquals(mockUser.getPassword(), userDetails.getPassword());
    assertTrue(userDetails instanceof UserDetailsImpl);

    verify(userRepository, times(1)).findByEmail(email);
  }

  @Test
  @DisplayName(
    "<UT>Should throw UsernameNotFoundException when user is not found"
  )
  // TO DO the UsernameNotFoundException is not yet implemented
  void testLoadUserByUsername_UserNotFound() {
    String email = "nobody@example.com";

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    UsernameNotFoundException exception = assertThrows(
      UsernameNotFoundException.class,
      () -> userDetailsService.loadUserByUsername(email)
    );

    assertEquals("User Not Found with email: " + email, exception.getMessage());

    verify(userRepository, times(1)).findByEmail(email);
  }
}
