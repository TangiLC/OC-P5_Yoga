package com.openclassrooms.starterjwt.payload.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SuiteDisplayName("PAYLOAD")
@DisplayName("Unit tests for MessageResponse")
class MessageResponseUnitTest {

  @DisplayName("Should handle different MessageResponse scenario ")
  @ParameterizedTest(name = "({index}) : {0}")
  @CsvSource(
    {
      "Regular string message, 'Initial message', 'Updated message', 'Updated message'",
      "Empty string message, '', 'New message', 'New message'",
      "Null initial message, , 'New message', 'New message'",
      "Update to null message, 'Initial message', , 'Initial message'",
    }
  )
  void should_handle_message_operations(
    String scenarioName,
    String initialMessage,
    String updatedMessage,
    String expectedFinalMessage
  ) {
    MessageResponse response = new MessageResponse(initialMessage);

    assertThat(response.getMessage()).isEqualTo(initialMessage);

    if (updatedMessage != null) {
      response.setMessage(updatedMessage);
    }

    assertThat(response.getMessage()).isEqualTo(expectedFinalMessage);
  }
}
