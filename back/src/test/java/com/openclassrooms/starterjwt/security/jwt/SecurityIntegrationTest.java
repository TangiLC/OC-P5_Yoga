package com.openclassrooms.starterjwt.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.platform.suite.api.SuiteDisplayName;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@SuiteDisplayName("SECURITY")
@DisplayName("¤Integration tests for Jwt Token")
class SecurityIntegrationTests {

  @Value("${oc.app.jwtSecret}")
  private String jwtSecret;

  @Value("${oc.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JwtUtils jwtUtils;

  private String generateToken(String username, boolean expired) {
    long currentTime = System.currentTimeMillis();
    return Jwts
      .builder()
      .setSubject(username)
      .setIssuedAt(new Date(expired ? currentTime - 10000 : currentTime))
      .setExpiration(
        new Date(expired ? currentTime - 5000 : currentTime + jwtExpirationMs)
      )
      .signWith(SignatureAlgorithm.HS512, jwtSecret)
      .compact();
  }

  @ParameterizedTest(name = "({index}) : {0} [{2}]")
  @DisplayName("Should handle different Token Auth scenario ")
  @CsvSource(
    {
      //"Valid token,               VALID,      200",
      "Expired token,             EXPIRED,    401",
      "Missing Bearer prefix,     MALFORMED,  401",
      "Empty authorization,       EMPTY,      401",
    }
  )
  void testJwtAuthorization(
    String scenarioName,
    String tokenType,
    int expectedStatus
  ) throws Exception {
    String token;
    if ("VALID".equals(tokenType)) {
      token = "Bearer " + generateToken("yoga@studio.com", false);
    } else if ("EXPIRED".equals(tokenType)) {
      token = "Bearer " + generateToken("yoga@studio.com", true);
    } else if ("MALFORMED".equals(tokenType)) {
      token = generateToken("user@test.com", false);
    } else {
      token = "";
    }

    mockMvc
      .perform(
        get("/api/session")
          .contentType(MediaType.APPLICATION_JSON)
          .header("Authorization", token)
      )
      .andExpect(status().is(expectedStatus));
  }

  @ParameterizedTest(name = "({index}) : {0} [{3}]")
  @DisplayName("Should handle different UserName extraction scenario ")
  @CsvSource(
    {
      "Valid username match,     user@test.com,     user@test.com,      true",
      "Invalid username match,   user@test.com,     other@test.com,     false",
      "Case sensitive check,     User@test.com,     user@test.com,      false",
    }
  )
  @WithMockUser
  void testUsernameExtraction(
    String scenario,
    String tokenUsername,
    String extractUsername,
    boolean shouldMatch
  ) {
    String token = generateToken(tokenUsername, false);
    String extracted = jwtUtils.getUserNameFromJwtToken(token);
    assertThat(extracted.equals(extractUsername)).isEqualTo(shouldMatch);
  }

  @ParameterizedTest(name = "({index}) : {0} [{2}]")
  @DisplayName("Should handle different Token validation scenario ")
  @CsvSource(
    {
      "Valid token,         VALID,      true",
      "Expired token,       EXPIRED,    false",
      "Malformed token,     MALFORMED,  false",
      "Empty token,         EMPTY,      false",
    }
  )
  void testTokenValidation(
    String scenarioName,
    String tokenType,
    boolean expectedValid
  ) {
    String token;
    if ("VALID".equals(tokenType)) {
      token = generateToken("user@test.com", false);
    } else if ("EXPIRED".equals(tokenType)) {
      token = generateToken("user@test.com", true);
    } else if ("MALFORMED".equals(tokenType)) {
      token = "invalidToken123";
    } else {
      token = "";
    }

    boolean isValid = !token.isEmpty() && jwtUtils.validateJwtToken(token);
    assertThat(isValid).isEqualTo(expectedValid);
  }

  @Test
  @DisplayName("Authentication Entry Point - Unauthorized Response")
  void testAuthenticationEntryPointResponse() throws Exception {
    mockMvc
      .perform(get("/api/test").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.status").value(401))
      .andExpect(jsonPath("$.error").value("Unauthorized"))
      .andExpect(jsonPath("$.path").exists())
      .andExpect(jsonPath("$.message").exists());
  }
}
