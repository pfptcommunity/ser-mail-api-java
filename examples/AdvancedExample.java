import io.pfpt.ser.mail.*;
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
public class AdvancedExample {

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

    // Construct logo_a attachment with dynamic content ID
    var logo_b =
        Attachment.builder()
            .fromFile("c:/temp/logo_b.png") // Load logo_b from file
            .dispositionInline() // Set dynamic content ID
            .build();

    // Use the fluent builder to construct the Message in a single chain
    Message message =
        Message.builder()
            .subject("This is a test email") // Sets the email subject (required)
            .from(new MailUser("sender@example.com", "Joe Sender")) // Sets the sender (required)
            .addContent(
                new Content(
                    "This is a test message",
                    Content.ContentType.TEXT)) // Adds plain text content (required minimum)
            .addContent(
                new Content( // Required: Adds HTML content referencing both static and dynamic CIDs
                    "<b>Static CID</b><br><img src=\"cid:logo\"><br><b>Dynamic CID</b><br><img src=\"cid:"
                        + logo_b.getContentId()
                        + "\">",
                    Content.ContentType
                        .HTML)) // Uses logo_b's auto-assigned content ID retrieved from
                                // getContentId()
            .addAttachment(
                Attachment.builder()
                    .fromFile("C:/temp/logo_a.png")
                    .dispositionInline("logo")
                    .build()) // Adds an inline attachment with content ID "logo"
            .addAttachment(logo_b) // Adds logo_b with its dynamically assigned content ID
            .addTo(
                new MailUser(
                    "recipient1@example.com",
                    "Recipient 1")) // Adds a primary recipient (required minimum)
            .addTo(
                new MailUser(
                    "recipient2@example.com", "Recipient 2")) // Adds a second primary recipient
            .addCc(new MailUser("cc1@example.com", "CC Recipient 1")) // Adds a CC recipient
            .addCc(new MailUser("cc2@example.com", "CC Recipient 2")) // Adds a second CC recipient
            .addBcc(new MailUser("bcc1@example.com", "BCC Recipient 1")) // Adds a BCC recipient
            .addBcc(
                new MailUser("bcc2@example.com", "BCC Recipient 2")) // Adds a second BCC recipient
            .addAttachment(
                Attachment.builder()
                    .fromBase64("VGhpcyBpcyBhIHRlc3Qh", "test.txt")
                    .build()) // Adds an attachment from Base64-encoded text
            .addAttachment(
                Attachment.builder()
                    .fromFile("C:/temp/file.csv")
                    .build()) // Adds an attachment from a file
            .addAttachment(
                Attachment.builder()
                    .fromBytes(new byte[] {1, 2, 3}, "bytes.txt")
                    .build()) // Adds an attachment from a byte array
            .headerFrom(
                new MailUser(
                    "fancysender@example.com", "Header From")) // Sets the header "From" field
            .addReplyTo(new MailUser("noreply@example.com", "No Reply")) // Sets a Reply-To address
            .build(); // Constructs the Message, enforcing required fields (from, tos, subject,
                      // content)

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
