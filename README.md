# Proofpoint Secure Email Relay Mail API Java Library
[![Maven Central Version](https://img.shields.io/maven-central/v/io.pfpt.ser/ser-mail-api)](https://central.sonatype.com/artifact/io.pfpt.ser/ser-mail-api)

This library implements all the functions of the SER Email Relay API via **Java**.

## Requirements

- **Java 11+**
- **HttpClient** (built-in in Java 11+)
- Active **Proofpoint SER API credentials**

### Installing the Package

You can include the library in your project using **Maven**:

```xml
<dependency>
    <groupId>io.pfpt.ser</groupId>
    <artifactId>ser-mail-api</artifactId>
    <version>x.x.x</version>
</dependency>
```

## Features

- **Send Emails**: Easily compose and send emails with minimal code using a fluent builder pattern.
- **Support for Attachments**:
    - Attach files from disk
    - Encode attachments as Base64
    - Send `byte[]` attachments
- **Support for Inline HTML Content**:
    - Using the syntax `<img src="cid:logo">`
    - Content-ID can be set manually or auto-generated
- **HTML & Plain Text Content**: Supports both plain text and HTML email bodies.
- **Recipient Management**: Add `To`, `CC`, and `BCC` recipients with ease.
- **Reply Management**: Add `Reply-To` addresses to redirect replies.

## Quick Start
```java
import io.pfpt.ser.mail.*;

public class Example {
  public static void main(String[] args) {
    // Initialize the Client with OAuth credentials from the config
    Client client = new Client("<client_id>", "<client_secret>");

    // Use the fluent builder to construct and send an email
    Message message = Message.builder()
            .subject("This is a test email") 
            .from(new MailUser("sender@example.com", "Joe Sender")) 
            .addContent(new Content("This is a test message", Content.ContentType.TEXT))
            .addTo(new MailUser("recipient1@example.com", "Recipient 1")) 
            .build();
    
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
```

## Advanced Emails

```java
import io.pfpt.ser.mail.*;

public class Example {
  public static void main(String[] args) {
    // Initialize the Client with OAuth credentials from the config
    Client client = new Client("<client_id>", "<client_secret>");
    
    // Construct logo_a attachment with dynamic content ID
    var logo_b = Attachment.builder()
            .fromFile("c:/temp/logo_b.png") // Load logo_b from file
            .dispositionInline() // Set dynamic content ID
            .build();

    // Use the fluent builder to construct the Message in a single chain
    Message message = Message.builder()
            .subject("This is a test email") // Sets the email subject (required)
            .from(new MailUser("sender@example.com", "Joe Sender")) // Sets the sender (required)
            .addContent(new Content("This is a test message", Content.ContentType.TEXT)) // Adds plain text content (required minimum)
            .addContent(new Content( // Required: Adds HTML content referencing both static and dynamic CIDs
                    "<b>Static CID</b><br><img src=\"cid:logo\"><br><b>Dynamic CID</b><br><img src=\"cid:" + logo_b.getContentId() + "\">",
                    Content.ContentType.HTML)) // Uses logo_b's auto-assigned content ID retrieved from getContentId()
            .addAttachment(Attachment.builder().fromFile("C:/temp/logo_a.png").dispositionInline("logo").build()) // Adds an inline attachment with content ID "logo"
            .addAttachment(logo_b) // Adds logo_b with its dynamically assigned content ID
            .addTo(new MailUser("recipient1@example.com", "Recipient 1")) // Adds a primary recipient (required minimum)
            .addTo(new MailUser("recipient2@example.com", "Recipient 2")) // Adds a second primary recipient
            .addCc(new MailUser("cc1@example.com", "CC Recipient 1")) // Adds a CC recipient
            .addCc(new MailUser("cc2@example.com", "CC Recipient 2")) // Adds a second CC recipient
            .addBcc(new MailUser("bcc1@example.com", "BCC Recipient 1")) // Adds a BCC recipient
            .addBcc(new MailUser("bcc2@example.com", "BCC Recipient 2")) // Adds a second BCC recipient
            .addAttachment(Attachment.builder().fromBase64("VGhpcyBpcyBhIHRlc3Qh", "test.txt").build()) // Adds an attachment from Base64-encoded text
            .addAttachment(Attachment.builder().fromFile("C:/temp/file.csv").build()) // Adds an attachment from a file
            .addAttachment(Attachment.builder().fromBytes(new byte[] {1, 2, 3}, "bytes.txt").build()) // Adds an attachment from a byte array
            .headerFrom(new MailUser("fancysender@example.com", "Header From")) // Sets the header "From" field
            .addReplyTo(new MailUser("noreply@example.com", "No Reply")) // Sets a Reply-To address
            .build(); // Constructs the Message, enforcing required fields (from, tos, subject, content)
    
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
```

## Attachment MIME Type Deduction Behavior

- When creating attachments, the library **automatically determines the MIME type** based on the file extension.
- If the MIME type cannot be determined, an exception is raised.

```java
// Create an attachment from disk; the MIME type will be "text/csv", and disposition will be "Disposition.Attachment"
Attachment.builder().fromFile("C:/temp/file.csv").build();

// This will throw an error, as the MIME type is unknown
Attachment.builder().fromFile("C:/temp/file.unknown").build();

// Create an attachment and specify the type information. The disposition will be "Disposition.Attachment", filename will be unknown.txt, and MIME type "text/plain"
Attachment.builder().fromFile("C:/temp/file.unknown").filename("unknown.txt").build();

// Create an attachment and specify the type information. The disposition will be "Disposition.Attachment", filename will be file.unknown, and MIME type "text/plain"
Attachment.builder().fromFile("C:/temp/file.unknown").mimeType("text/plain").build();
```

## Inline Attachments and Content-IDs

When creating attachments, they are `Disposition.Attachment` by default. To use a **Content-ID** (e.g., `<img src="cid:logo">`) in HTML content, set the disposition to `Disposition.Inline`. The library supports both manual and auto-generated content IDs.

### Using a Dynamically Generated Content-ID
```java
// Create an inline attachment with an auto-generated content ID
Attachment logo = Attachment.builder().fromFile("C:/temp/logo.png").dispositionInline().build();
// Use the dynamic content ID in HTML content
Message message = Message.builder()
        .subject("Dynamic CID Test")
        .from(new MailUser("sender@example.com"))
        .addTo(new MailUser("recipient@example.com"))
        .addContent(new Content("<b>Test</b><br><img src=\"cid:" + logo.getContentId() + "\">", Content.ContentType.HTML))
        .addAttachment(logo)
        .build();
```

### Setting a Custom Content-ID
```java
Message message = Message.builder()
        .subject("Static CID Test")
        .from(new MailUser("sender@example.com"))
        .addTo(new MailUser("recipient@example.com"))
        .addContent(new Content("<b>Test</b><br><img src=\"cid:logo\">", Content.ContentType.HTML))
        .addAttachment(Attachment.builder().fromFile("C:/temp/logo.png").dispositionInline("logo").build())
        .build();
```

### Concurrency Example
```java
ExecutorService executorService = Executors.newFixedThreadPool(10);
Client client = new Client("<client_id>", "<client_secret>");
for (int i = 0; i < 10; i++) {
    executorService.submit(() -> {
        Message msg = Message.builder()
            .subject("Concurrent Test")
            .from(new MailUser("sender@example.com"))
            .addTo(new MailUser("recipient@example.com"))
            .addContent(new Content("Test message", Content.ContentType.TEXT))
            .build();
        SendResult result = client.send(msg).join();
        System.out.printf("Thread [%d] Status: %d\n", Thread.currentThread().getId(), result.getHttpResponse().statusCode());
    });
}
executorService.shutdown();
executorService.awaitTermination(60, TimeUnit.SECONDS);
```

## Known Issues

There is a known issue where **empty file content** results in a **400 Bad Request** error.

```json
{
  "content": "",
  "disposition": "attachment",
  "filename": "empty.txt",
  "id": "1ed38149-70b2-4476-84a1-83e73913d43c",
  "type": "text/plain"
}
```

🔹 **API Response:**

```
Status Code: 400/BadRequest
Reason: attachments[0].content is required
Message ID:
Request ID: fe9a1acf60a20c9d90bed843f6530156
Raw JSON: {"request_id":"fe9a1acf60a20c9d90bed843f6530156","reason":"attachments[0].content is required"}
```

This issue has been reported to **Proofpoint Product Management**.

## Limitations
- The Proofpoint API currently does not support **empty file attachments**.
- If an empty file is sent, you will receive a **400 Bad Request** error.

## Additional Resources
For more information, refer to the official **Proofpoint Secure Email Relay API documentation**:  
[**API Documentation**](https://api-docs.ser.proofpoint.com/docs/email-submission)
