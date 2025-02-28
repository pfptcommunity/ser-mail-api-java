package io.pfpt.ser.mail;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * An example demonstrating email sending using the Proofpoint SER API with a fluent builder. This
 * class constructs a Message object using the modern builder pattern, showcasing a concise and
 * readable approach to email creation with build-time validation.
 */
public class BasicExample {

  /**
   * Loads configuration from a JSON file into a Map. Reads client credentials (e.g., client_id,
   * client_secret) from a file for Client initialization.
   *
   * @param filePath the path to the configuration file (e.g., "ser.api_key")
   * @return a Map containing the configuration key-value pairs parsed from JSON
   * @throws RuntimeException if the file cannot be read or JSON parsing fails
   */
  public static Map<String, String> loadConfig(String filePath) {
    Jsonb jsonb = JsonbBuilder.create();
    try (FileReader reader = new FileReader(filePath)) {
      return jsonb.fromJson(reader, Map.class);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load configuration from file: " + filePath, e);
    }
  }

  /**
   * Main method demonstrating email construction and sending via the Proofpoint SER API. Utilizes
   * the fluent builder pattern for Message creation, ensuring required fields are set.
   *
   * @param args command-line arguments (not used in this example)
   */
  public static void main(String[] args) {
    // Load client credentials from a configuration file
    Map<String, String> config = loadConfig("ser.api_key");

    // Initialize the Client with OAuth credentials from the config
    Client client = new Client(config.get("client_id"), config.get("client_secret"));

    // Use the fluent builder to construct and send an email
    Message message =
        Message.builder()
            .subject("This is a test email")
            .from("sender@example.com", "Joe Sender")
            .addContent("This is a test message", Content.ContentType.TEXT)
            .addTo("recipient1@example.com", "Recipient 1")
            .build();

    // Print the JSON representation of the Message for debugging
    System.out.println(message);

    // Send the message asynchronously and wait for the result
    SendResult sendResult = client.send(message).join();
    // Output the HTTP status code from the API response
    System.out.println("HTTP Status: " + sendResult.getHttpResponse().statusCode());
    // Output the message ID from the API response
    System.out.println("Message ID: " + sendResult.getMessageId());
    // Output the reason (if any) from the API response
    System.out.println("Reason: " + sendResult.getReason());
    // Output the request ID from the API response
    System.out.println("Request ID: " + sendResult.getRequestId());
  }
}
