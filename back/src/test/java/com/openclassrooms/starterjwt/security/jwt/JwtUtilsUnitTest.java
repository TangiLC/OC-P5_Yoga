package com.openclassrooms.starterjwt.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

class JwtUtilsUnitTest {

  private JwtUtils jwtUtils;

  @Mock
  private Authentication authentication;

  private final String jwtSecret =
    "Here_Is4LongMockedKeyToPerform_HS512!Encryption.";
  private final int jwtExpirationMs = 60000;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    jwtUtils = new JwtUtils();
    jwtUtils.jwtSecret = jwtSecret;
    jwtUtils.jwtExpirationMs = jwtExpirationMs;
  }

  @Test
  @DisplayName("Should generate a valid JWT token")
  void generateJwtToken_ShouldGenerateValidToken() {
    UserDetailsImpl userDetails = new UserDetailsImpl(
      1L,
      "testUser",
      "testPassword",
      "test@example.com",
      null,
      jwtSecret
    );
    when(authentication.getPrincipal()).thenReturn(userDetails);

    String token = jwtUtils.generateJwtToken(authentication);

    assertThat(token).isNotNull();
    assertThat(jwtUtils.validateJwtToken(token)).isTrue();
    assertThat(jwtUtils.getUserNameFromJwtToken(token)).isEqualTo("testUser");
  }

  @Test
  @DisplayName("Should return the username from a valid JWT token")
  void getUserNameFromJwtToken_ShouldReturnUsername() {
    UserDetailsImpl userDetails = new UserDetailsImpl(
      1L,
      "testUser",
      "testPassword",
      "test@example.com",
      null,
      jwtSecret
    );
    String token = generateTestToken(userDetails.getUsername());

    String username = jwtUtils.getUserNameFromJwtToken(token);

    assertThat(username).isEqualTo("testUser");
  }

  @Test
  @DisplayName("Should validate a valid JWT token")
  void validateJwtToken_ShouldReturnTrueForValidToken() {
    UserDetailsImpl userDetails = new UserDetailsImpl(
      1L,
      "testUser",
      "testPassword",
      "test@example.com",
      null,
      jwtSecret
    );
    String token = generateTestToken(userDetails.getUsername());

    boolean isValid = jwtUtils.validateJwtToken(token);

    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("Should return false for an expired JWT token")
  void validateJwtToken_ShouldReturnFalseForExpiredToken() {
    String expiredToken = generateExpiredToken("testUser");

    boolean isValid = jwtUtils.validateJwtToken(expiredToken);

    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("Should return false for a malformed JWT token")
  void validateJwtToken_ShouldReturnFalseForMalformedToken() {
    String malformedToken = "invalid.token";

    boolean isValid = jwtUtils.validateJwtToken(malformedToken);

    assertThat(isValid).isFalse();
  }

  private String generateTestToken(String username) {
    return Jwts
      .builder()
      .setSubject(username)
      .setIssuedAt(new Date())
      .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
      .signWith(SignatureAlgorithm.HS512, jwtSecret)
      .compact();
  }

  private String generateExpiredToken(String username) {
    return Jwts
      .builder()
      .setSubject(username)
      .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
      .setExpiration(new Date(System.currentTimeMillis() - 5000))
      .signWith(SignatureAlgorithm.HS512, jwtSecret)
      .compact();
  }

  @Test
  @DisplayName("Should validate JWT tokens correctly for different scenarios")
  void validateJwtToken_ShouldValidateCorrectly() {
    String validToken = generateTestToken("testUser");
    String expiredToken = generateExpiredToken("testUser");
    String malformedToken = "invalid.token";
    String invalidToken = Jwts
      .builder()
      .setSubject("testUser")
      .signWith(SignatureAlgorithm.HS512, "AnotherKeyButNotTheExpectedOne")
      .compact();

    assertThat(jwtUtils.validateJwtToken(validToken)).isTrue();
    assertThat(jwtUtils.validateJwtToken(expiredToken)).isFalse();
    assertThat(jwtUtils.validateJwtToken(malformedToken)).isFalse();
    assertThat(jwtUtils.validateJwtToken(null)).isFalse();
    assertThat(jwtUtils.validateJwtToken(invalidToken)).isFalse();
  }
}
