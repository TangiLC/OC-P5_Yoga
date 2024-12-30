package com.openclassrooms.starterjwt.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  public UserServiceTest() {
    MockitoAnnotations.openMocks(this);
  }

  private static User createUser(Long id, String email) {
    User user = new User();
    user.setId(id);
    user.setEmail(email);
    return user;
  }

  @Test
  @DisplayName("Should delete a user by ID")
  void delete_ShouldDeleteUserById() {
    
    Long userId = 1L;

    userService.delete(userId);

    verify(userRepository, times(1)).deleteById(userId);
  }

  @Test
  @DisplayName("Should return a user by ID when it exists")
  void findById_ShouldReturnUser_WhenExists() {
 
    Long userId = 1L;
    User user = createUser(userId, "test@example.com");
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    User result = userService.findById(userId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(userId);
    assertThat(result.getEmail()).isEqualTo("test@example.com");
    verify(userRepository, times(1)).findById(userId);
  }

  @Test
  @DisplayName("Should return null when user does not exist")
  void findById_ShouldReturnNull_WhenNotExists() {

    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    User result = userService.findById(userId);

    assertThat(result).isNull();
    verify(userRepository, times(1)).findById(userId);
  }
}
