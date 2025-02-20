package test.com.proofpoint.secureemailrelay.mail;

import com.proofpoint.secureemailrelay.mail.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

public class Example {

  public static Map<String, String> loadConfig(String filePath) {
    Jsonb jsonb = JsonbBuilder.create();
    try (FileReader reader = new FileReader(filePath)) {
      return jsonb.fromJson(reader, Map.class);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load configuration from file: " + filePath, e);
    }
  }

  public static void main(String[] args) {
    Map<String, String> config = loadConfig("ser.api_key");

    Client client = new Client(config.get("client_id"), config.get("client_secret"));

    Message message =
        new Message("This is a test email", new MailUser("sender@example.com", "Joe Sender"));

    // Add text content body
    message.addContent(new Content("This is a test message", Content.ContentType.TEXT));

    // Add HTML content body, with embedded image
    message.addContent(
        new Content(
            "<b>This is a test message</b><br><img src=\"cid:logo\">", Content.ContentType.HTML));

    // Create an inline attachment from disk and set the cid
    message.addAttachment(
        Attachment.builder().fromFile("C:/temp/logo.png").dispositionInline("logo").build());

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
    message.addAttachment(
        Attachment.builder().fromBase64("VGhpcyBpcyBhIHRlc3Qh", "test.txt").build());

    message.addAttachment(Attachment.builder().fromFile("C:/temp/file.csv").build());

    message.addAttachment(
        Attachment.builder().fromBytes(new byte[] {1, 2, 3}, "bytes.txt").build());

    message.setHeaderFrom(new MailUser("fancysender@proofpoint.com", "Header From"));

    // Set Reply-To
    message.addReplyTo(new MailUser("noreply@proofpoint.com", "No Reply"));

    System.out.println(message);

    SendResult sendResult = client.send(message).join();
    System.out.println("HTTP Status: " + sendResult.getHttpResponse().statusCode());
    System.out.println("Message ID: " + sendResult.getMessageId());
    System.out.println("Reason: " + sendResult.getReason());
    System.out.println("Request ID: " + sendResult.getRequestId());
  }
}
