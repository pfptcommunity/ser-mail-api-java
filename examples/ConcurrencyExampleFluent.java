package io.pfpt.ser.mail;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

/**
 * An example demonstrating concurrent email sending using the Proofpoint SER API with a fluent builder.
 * This class uses a thread pool to send multiple emails simultaneously, constructing each Message
 * with the fluent builder pattern for clarity and build-time validation.
 */
public class ConcurrencyExampleFluent {

    /**
     * Loads configuration from a JSON file into a Map.
     * Reads client credentials (e.g., client_id, client_secret) for Client initialization.
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
     * Main method demonstrating concurrent email sending with a thread pool.
     * Each thread constructs and sends a Message using the fluent builder pattern.
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

                            // Construct a Message using the fluent builder pattern
                            Message message = Message.builder()
                                    .subject("This is a test email") // Required: Sets the email subject
                                    .from(new MailUser("sender@example.com", "Joe Sender")) // Required: Sets the sender
                                    .addContent(new Content("This is a test message", Content.ContentType.TEXT)) // Required: Adds plain text content
                                    .addContent(new Content("<b>This is a test message</b><br><img src=\"cid:logo\">", Content.ContentType.HTML)) // Adds HTML content with embedded image
                                    .addAttachment(Attachment.builder().fromFile("C:/temp/logo.png").dispositionInline("logo").build()) // Adds an inline attachment with content ID "logo"
                                    .addTo(new MailUser("recipient1@example.com", "Recipient 1")) // Required: Adds a primary recipient
                                    .addTo(new MailUser("recipient2@example.com", "Recipient 2")) // Adds a second primary recipient
                                    .addCc(new MailUser("cc1@example.com", "CC Recipient 1")) // Adds a CC recipient
                                    .addCc(new MailUser("cc2@example.com", "CC Recipient 2")) // Adds a second CC recipient
                                    .addBcc(new MailUser("bcc1@example.com", "BCC Recipient 1")) // Adds a BCC recipient
                                    .addBcc(new MailUser("bcc2@example.com", "BCC Recipient 2")) // Adds a second BCC recipient
                                    .addAttachment(Attachment.builder().fromBase64("VGhpcyBpcyBhIHRlc3Qh", "test.txt").build()) // Adds an attachment from Base64-encoded text
                                    .addAttachment(Attachment.builder().fromBase64("VGhpcyBpcyBhIHRlc3Qh", "test.txt").build()) // Adds a duplicate Base64 attachment
                                    .addAttachment(Attachment.builder().fromBytes(new byte[] {1, 2, 3}, "bytes.txt").build()) // Adds an attachment from a byte array
                                    .addReplyTo(new MailUser("noreply@proofpoint.com", "No Reply")) // Sets a Reply-To address
                                    .build(); // Constructs the Message, enforcing required fields

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