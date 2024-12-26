package com.openclassrooms.starterjwt.security.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;

class UserDetailsImplTest {

  @Mock
  private UserDetailsImpl mockedUserDetails;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("<UT>Should return correct values from mocked UserDetailsImpl")
  void testMockedUserDetails() {
    when(mockedUserDetails.getId()).thenReturn(1L);
    when(mockedUserDetails.getUsername()).thenReturn("test@example.com");
    when(mockedUserDetails.getFirstName()).thenReturn("John");
    when(mockedUserDetails.getLastName()).thenReturn("Doe");
    when(mockedUserDetails.getAdmin()).thenReturn(true);
    when(mockedUserDetails.getPassword()).thenReturn("securePassword");

    assertEquals(1L, mockedUserDetails.getId());
    assertEquals("test@example.com", mockedUserDetails.getUsername());
    assertEquals("John", mockedUserDetails.getFirstName());
    assertEquals("Doe", mockedUserDetails.getLastName());
    assertTrue(mockedUserDetails.getAdmin());
    assertEquals("securePassword", mockedUserDetails.getPassword());
  }

  @Test
  @DisplayName("<UT>Should return empty authorities from UserDetailsImpl")
  void testGetAuthorities() {
    UserDetailsImpl userDetails = UserDetailsImpl.builder().build();

    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

    assertNotNull(authorities);
    assertTrue(authorities.isEmpty());
  }

  @Test
  @DisplayName("<UT>Should return true for all account status methods")
  void testAllAccountStatusMethods() {
    UserDetailsImpl userDetails = UserDetailsImpl.builder().build();

    /*WARN : Actual code do not properly handle expire/lock...
     * TO DO modify and test again when changes in UserDetailsImpl.java
     */
    assertTrue(userDetails.isAccountNonExpired());
    assertTrue(userDetails.isAccountNonLocked());
    assertTrue(userDetails.isCredentialsNonExpired());
    assertTrue(userDetails.isEnabled());
  }

  @Test
  @DisplayName("<UT>Should verify that equals method returns correct answer")
  void testEquals() {
    UserDetailsImpl dude = UserDetailsImpl.builder().id(1L).build();
    UserDetailsImpl sameDude = UserDetailsImpl.builder().id(1L).build();
    UserDetailsImpl otherDude = UserDetailsImpl.builder().id(2L).build();

    assertTrue(dude.equals(dude));
    assertTrue(dude.equals(sameDude));
    assertFalse(dude.equals(otherDude));
    assertFalse(dude.equals(null));
    assertFalse(dude.equals(new Object()));
  }
}
