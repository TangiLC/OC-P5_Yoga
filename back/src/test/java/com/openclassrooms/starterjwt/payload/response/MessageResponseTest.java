package com.openclassrooms.starterjwt.payload.response;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MessageResponseTest {

  @Test
  @DisplayName("<UT>Should instantiate MessageResponse without errors")
  void testInstantiation() {
    MessageResponse messageResponse = new MessageResponse("Test message");
    assertNotNull(messageResponse);
  }

  @Test
  @DisplayName("<UT>Should correctly set and get the message using constructor")
  void testConstructorAndGetMessage() {
    String expectedMessage = "Constructor message";

    MessageResponse messageResponse = new MessageResponse(expectedMessage);

    assertEquals(expectedMessage, messageResponse.getMessage());
  }

  @Test
  @DisplayName("<UT>Should correctly set and get the message using setter")
  void testSetMessage() {
    String expectedMessage = "Set/Get message";

    MessageResponse messageResponse = new MessageResponse(null);

    messageResponse.setMessage(expectedMessage);
    assertEquals(expectedMessage, messageResponse.getMessage());
  }
}
