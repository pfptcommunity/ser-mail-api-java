package test.com.proofpoint.secureemailrelay.mail;

import com.proofpoint.secureemailrelay.mail.Client;
import com.proofpoint.secureemailrelay.mail.Message;
import com.proofpoint.secureemailrelay.mail.MailUser;
import com.proofpoint.secureemailrelay.mail.Attachment;
import com.proofpoint.secureemailrelay.mail.Content;
import com.proofpoint.secureemailrelay.mail.SendResult;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrencyTest {
    public static Map<String, String> loadConfig(String filePath) {
        Jsonb jsonb = JsonbBuilder.create();
        try (FileReader reader = new FileReader(filePath)) {
            return jsonb.fromJson(reader, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration from file: " + filePath, e);
        }
    }

    public static void main(String[] args) {
        final int THREAD_COUNT = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        Map<String, String> config = loadConfig("ser.api_key");

        Client client = new Client(config.get("client_id"), config.get("client_secret"));

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    Message message = new Message("This is a test email", new MailUser("sender@example.com", "Joe Sender"));

                    // Add text content body
                    message.addContent(new Content("This is a test message", Content.ContentType.TEXT));

                    // Add HTML content body, with embedded image
                    message.addContent(new Content("<b>This is a test message</b><br><img src=\"cid:logo\">", Content.ContentType.HTML));

                    // Create an inline attachment from disk and set the cid
                    message.addAttachment(Attachment.builder().
                            fromFile("C:/temp/logo.png")
                            .dispositionInline("logo")
                            .build());

                    // Add recipients
                    message.addTo(new MailUser("recipient1@example.com", "Recipient 1"));
                    message.addTo(new MailUser("recipient2@example.com", "Recipient 2"));

                    // Add CC
                    message.addCc(new MailUser("cc1@example.com", "CC Recipient 1"));
                    message.addCc(new MailUser("cc2@example.com", "CC Recipient 2"));

                    // Add BCC
                    message.addBcc(new MailUser("bcc1@example.com", "BCC Recipient 1"));
                    message.addBcc(new MailUser("bcc2@example.com", "BCC Recipient 2"));

                    // Add attachments
                    message.addAttachment(Attachment.builder()
                            .fromBase64("VGhpcyBpcyBhIHRlc3Qh", "test.txt")
                            .build());

                    message.addAttachment(Attachment.builder()
                            .fromBase64("VGhpcyBpcyBhIHRlc3Qh", "test.txt")
                            .build());

                    message.addAttachment(Attachment.builder()
                            .fromBytes(new byte[]{1, 2, 3}, "bytes.txt")
                            .build());

                    // Set Reply-To
                    message.addReplyTo(new MailUser("noreply@proofpoint.com", "No Reply"));

                    System.out.println(message.toString());
                    SendResult sendResult = client.send(message).join();
                    System.out.println("HTTP Status: " + sendResult.getHttpResponse().statusCode());
                    System.out.println("Message ID: " + sendResult.getMessageId());
                    System.out.println("Reason: " + sendResult.getReason());
                    System.out.println("Request ID: " + sendResult.getRequestId());
                } catch (Exception e) {
                    System.err.println("Thread: " + Thread.currentThread().getName() + " - Exception: " + e.getMessage());
                }
            });
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}