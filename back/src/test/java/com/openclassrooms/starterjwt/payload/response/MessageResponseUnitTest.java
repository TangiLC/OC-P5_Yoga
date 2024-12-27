package com.openclassrooms.starterjwt.payload.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MessageResponseTest {

  @DisplayName("Should handle different MessageResponse scenarios")
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideMessageScenarios")
  void should_handle_message_operations(
    String testName,
    String initialMessage,
    String updatedMessage,
    String expectedFinalMessage
  ) {
    MessageResponse response = new MessageResponse(initialMessage);

    assertThat(response.getMessage()).isEqualTo(initialMessage);

    if (updatedMessage != null) {
      response.setMessage(updatedMessage);

      assertThat(response.getMessage()).isEqualTo(expectedFinalMessage);
    }
  }

  private static Stream<Arguments> provideMessageScenarios() {
    return Stream.of(
      Arguments.of(
        "Should handle regular string message",
        "Initial message",
        "Updated message",
        "Updated message"
      ),
      Arguments.of(
        "Should handle empty string message",
        "",
        "New message",
        "New message"
      ),
      Arguments.of(
        "Should handle null initial message",
        null,
        "New message",
        "New message"
      ),
      Arguments.of(
        "Should handle update to null message",
        "Initial message",
        null,
        null
      ),
      Arguments.of(
        "Should maintain initial message when no update",
        "Initial message",
        null,
        "Initial message"
      )
    );
  }
}
