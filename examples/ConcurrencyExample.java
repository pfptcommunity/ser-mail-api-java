import io.pfpt.ser.mail.*;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * An example demonstrating concurrent email sending using the Proofpoint SER API with a fluent
 * builder. This class uses a thread pool to send multiple emails simultaneously, constructing each
 * Message with the fluent builder pattern for clarity and build-time validation.
 */
public class ConcurrencyExample {

  /**
   * Loads configuration from a JSON file into a Map. Reads client credentials (e.g., client_id,
   * client_secret) for Client initialization.
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
   * Main method demonstrating concurrent email sending with a thread pool. Each thread constructs
   * and sends a Message using the fluent builder pattern.
   *
   * @param args command-line arguments (not used in this example)
   */
  public static void main(String[] args) {
    // Number of threads for concurrent email sending
    final int THREAD_COUNT = 10;
    // Create a fixed-size thread pool to manage concurrent tasks
    ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

    // Load client credentials from a configuration file
    Map<String, String> config = loadConfig("ser.api_key");

    // Initialize a single Client instance shared across threads
    Client client = new Client(config.get("client_id"), config.get("client_secret"));

    // Submit email sending tasks to the thread pool
    for (int i = 0; i < THREAD_COUNT; i++) {
      executorService.submit(
          () -> {
            try {
              // Capture the current thread ID for logging
              long tid = Thread.currentThread().getId();

              // Construct logo_a attachment with dynamic content ID
              var logo_b =
                  Attachment.builder()
                      .fromFile("c:/temp/logo_b.png") // Load logo_b from file
                      .dispositionInline() // Set dynamic content ID
                      .build();

              // Use the fluent builder to construct the Message in a single chain
              // Use the fluent builder to construct the Message in a single chain
              Message message =
                  Message.builder()
                      .subject("This is a test email") // Sets the email subject (required)
                      .from("sender@example.com", "Joe Sender") // Sets the sender (required)
                      .addContent(
                          "This is a test message",
                          Content.ContentType.TEXT) // Adds plain text content (required minimum)
                      .addContent( // Required: Adds HTML content referencing both static and
                          // dynamic CIDs
                          "<b>Static CID</b><br><img src=\"cid:logo\"><br><b>Dynamic CID</b><br><img src=\"cid:"
                              + logo_b.getContentId()
                              + "\">",
                          Content.ContentType
                              .HTML) // Uses logo_b's auto-assigned content ID retrieved from
                      // getContentId()
                      .addAttachment(
                          Attachment.builder()
                              .fromFile("C:/temp/logo_a.png")
                              .dispositionInline("logo")
                              .build()) // Adds an inline attachment with content ID "logo"
                      .addAttachment(logo_b) // Adds logo_b with its dynamically assigned content ID
                      .addTo(
                          "recipient1@example.com",
                          "Recipient 1") // Adds a primary recipient (required minimum)
                      .addTo(
                          "recipient2@example.com",
                          "Recipient 2") // Adds a second primary recipient
                      .addCc("cc1@example.com", "CC Recipient 1") // Adds a CC recipient
                      .addCc("cc2@example.com", "CC Recipient 2") // Adds a second CC recipient
                      .addBcc("bcc1@example.com", "BCC Recipient 1") // Adds a BCC recipient
                      .addBcc("bcc2@example.com", "BCC Recipient 2") // Adds a second BCC recipient
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
                          "fancysender@example.com", "Header From") // Sets the header "From" field
                      .addReplyTo("noreply@example.com", "No Reply") // Sets a Reply-To address
                      .build(); // Constructs the Message, enforcing required fields (from, tos,
              // subject, content)

              // Send the message asynchronously and wait for the result
              SendResult sendResult = client.send(message).join();
              // Log the HTTP status code with thread ID
              System.out.printf(
                  "[%d]HTTP Status: %d\n", tid, sendResult.getHttpResponse().statusCode());
              // Log the message ID with thread ID
              System.out.printf("[%d]Message ID: %s\n", tid, sendResult.getMessageId());
              // Log the reason (if any) with thread ID
              System.out.printf("[%d]Reason: %s\n", tid, sendResult.getReason());
              // Log the request ID with thread ID
              System.out.printf("[%d]Request ID: %s\n", tid, sendResult.getRequestId());
            } catch (Exception e) {
              // Log any exceptions with thread name and message for debugging
              System.err.println(
                  "Thread: "
                      + Thread.currentThread().getName()
                      + " - Exception: "
                      + e.getMessage());
            }
          });
    }

    // Initiate graceful shutdown of the thread pool
    executorService.shutdown();
    try {
      // Wait up to 60 seconds for all tasks to complete
      if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
        // Force shutdown if tasks don't complete in time
        executorService.shutdownNow();
      }
    } catch (InterruptedException e) {
      // Force shutdown and restore interrupted status if interrupted
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
