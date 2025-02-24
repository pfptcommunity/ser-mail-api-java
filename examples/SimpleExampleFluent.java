package io.pfpt.ser.mail;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class SimpleExampleFluent {

    /**
     * Loads configuration from a JSON file into a Map.
     *
     * @param filePath the path to the configuration file
     * @return a Map containing the configuration key-value pairs
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

    public static void main(String[] args) {
        Map<String, String> config = loadConfig("ser.api_key");

        Client client = new Client(config.get("client_id"), config.get("client_secret"));

        // Use the fluent builder to construct the Message
        Message message = Message.builder()
                .subject("This is a test email")
                .from(new MailUser("sender@example.com", "Joe Sender"))
                .addContent(new Content("This is a test message", Content.ContentType.TEXT))
                .addContent(new Content("<b>This is a test message</b><br><img src=\"cid:logo\">", Content.ContentType.HTML))
                .addAttachment(Attachment.builder().fromFile("C:/temp/logo.png").dispositionInline("logo").build())
                .addTo(new MailUser("recipient1@example.com", "Recipient 1"))
                .addTo(new MailUser("recipient2@example.com", "Recipient 2"))
                .addCc(new MailUser("cc1@example.com", "CC Recipient 1"))
                .addCc(new MailUser("cc2@example.com", "CC Recipient 2"))
                .addBcc(new MailUser("bcc1@example.com", "BCC Recipient 1"))
                .addBcc(new MailUser("bcc2@example.com", "BCC Recipient 2"))
                .addAttachment(Attachment.builder().fromBase64("VGhpcyBpcyBhIHRlc3Qh", "test.txt").build())
                .addAttachment(Attachment.builder().fromFile("C:/temp/file.csv").build())
                .addAttachment(Attachment.builder().fromBytes(new byte[] {1, 2, 3}, "bytes.txt").build())
                .headerFrom(new MailUser("fancysender@proofpoint.com", "Header From"))
                .addReplyTo(new MailUser("noreply@proofpoint.com", "No Reply"))
                .build();

        System.out.println(message);

        SendResult sendResult = client.send(message).join();
        System.out.println("HTTP Status: " + sendResult.getHttpResponse().statusCode());
        System.out.println("Message ID: " + sendResult.getMessageId());
        System.out.println("Reason: " + sendResult.getReason());
        System.out.println("Request ID: " + sendResult.getRequestId());
    }
}