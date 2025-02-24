package io.pfpt.ser.mail;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

/**
 * A legacy example demonstrating how to send an email using the Proofpoint SER API. This class
 * constructs a Message object using the traditional constructor and add methods, showcasing basic
 * email functionality including content, attachments, and recipients.
 */
public class SimpleExample {

  /**
   * Loads configuration from a JSON file into a Map. Reads client credentials from a file for use
   * in initializing the Client.
   *
   * @param filePath the path to the configuration file (e.g., "ser.api_key")
   * @return a Map containing key-value pairs from the JSON file
   * @throws RuntimeException if the file cannot be read or parsed
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
   * Main method demonstrating email construction and sending via the Proofpoint SER API. Uses
   * legacy constructor-based Message creation with incremental additions.
   *
   * @param args command-line arguments (not used in this example)
   */
  public static void main(String[] args) {
    // Load client credentials from a configuration file
    Map<String, String> config = loadConfig("ser.api_key");

    // Initialize the Client with OAuth credentials from the config
    Client client = new Client(config.get("client_id"), config.get("client_secret"));

    // Create a basic Message with subject and sender
    Message message =
        new Message("This is a test email", new MailUser("sender@example.com", "Joe Sender"));

    // Add text content body
    message.addContent(new Content("This is a test message", Content.ContentType.TEXT));

    // Add HTML content body with an embedded image referenced by content ID
    message.addContent(
        new Content(
            "<b>This is a test message</b><br><img src=\"cid:logo\">", Content.ContentType.HTML));

    // Create an inline attachment from a file on disk, setting its content ID to "logo"
    message.addAttachment(
        Attachment.builder().fromFile("C:/temp/logo_a.png").dispositionInline("logo").build());

    // Add primary recipients (To field)
    message.addTo(new MailUser("recipient1@example.com", "Recipient 1"));
    message.addTo(new MailUser("recipient2@example.com", "Recipient 2"));

    // Add CC recipients
    message.addCc(new MailUser("cc1@example.com", "CC Recipient 1"));
    message.addCc(new MailUser("cc2@example.com", "CC Recipient 2"));

    // Add BCC recipients
    message.addBcc(new MailUser("bcc1@example.com", "BCC Recipient 1"));
    message.addBcc(new MailUser("bcc2@example.com", "BCC Recipient 2"));

    // Add additional attachments using various construction methods
    message.addAttachment(
        Attachment.builder()
            .fromBase64("VGhpcyBpcyBhIHRlc3Qh", "test.txt")
            .build()); // Base64-encoded text
    message.addAttachment(
        Attachment.builder().fromFile("C:/temp/file.csv").build()); // File from disk
    message.addAttachment(
        Attachment.builder().fromBytes(new byte[] {1, 2, 3}, "bytes.txt").build()); // Byte array

    // Set the header "From" field to override the sender in the email headers
    message.setHeaderFrom(new MailUser("fancysender@example.com", "Header From"));

    // Set a Reply-To address
    message.addReplyTo(new MailUser("noreply@example.com", "No Reply"));

    // Print the JSON representation of the Message for debugging
    System.out.println(message);

    // Send the message asynchronously and wait for the result
    SendResult sendResult = client.send(message).join();
    // Output the HTTP status code from the response
    System.out.println("HTTP Status: " + sendResult.getHttpResponse().statusCode());
    // Output the message ID from the API response
    System.out.println("Message ID: " + sendResult.getMessageId());
    // Output the reason (if any) from the API response
    System.out.println("Reason: " + sendResult.getReason());
    // Output the request ID from the API response
    System.out.println("Request ID: " + sendResult.getRequestId());
  }
}
