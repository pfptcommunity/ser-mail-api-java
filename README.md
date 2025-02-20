# Proofpoint Secure Email Relay Mail API Java Library

This library implements all the functions of the SER Email Relay API via **Java**.

## Requirements

- **Java 11+**
- **HttpClient** (built-in in Java 11+)
- Active **Proofpoint SER API credentials**

### Installing the Package

You can include the library in your project using **Maven**:

```xml
<dependency>
    <groupId>com.proofpoint.secureemailrelay</groupId>
    <artifactId>mail</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Features

- **Send Emails**: Easily compose and send emails with minimal code.
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
    Map<String, String> config = loadConfig("ser.api_key");

    Client client = new Client(config.get("client_id"), config.get("client_secret"));

    Message message = new Message("This is a test email", new MailUser("sender@example.com", "Joe Sender"));

    // Add text content body
    message.addContent(new Content("This is a test message", Content.ContentType.TEXT));

    // Add HTML content body, with embedded image
    message.addContent(new Content("<b>This is a test message</b><br><img src=\"cid:logo\">", Content.ContentType.HTML));

    // Create an inline attachment from disk and set the cid
    message.addAttachment(Attachment.builder().fromFile("C:/temp/logo.png").dispositionInline("logo").build());

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
    message.addAttachment(Attachment.builder().fromBase64("VGhpcyBpcyBhIHRlc3Qh", "test.txt").build());
    message.addAttachment(Attachment.builder().fromFile("C:/temp/file.csv").build());
    message.addAttachment(Attachment.builder().fromBytes(new byte[]{1, 2, 3}, "bytes.txt").build());

    // Set Reply-To
    message.addReplyTo(new MailUser("noreply@proofpoint.com", "No Reply"));

    // Send the email
    SendResult sendResult = client.send(message).join();

    System.out.println("HTTP Status: " + sendResult.getHttpResponse().statusCode());
    System.out.println("Reason: " + sendResult.getReason());
    System.out.println("Message ID: " + sendResult.getMessageId());
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

When creating attachments, they are `Disposition.Attachment` by default. To properly reference a **Content-ID** (e.g.,
`<img src="cid:logo">`), you must explicitly set the attachment disposition to `Disposition.Inline`.
If the attachment type is set to `Disposition.Inline`, a default unique **Content-ID** will be generated.

### Using a Dynamically Generated Content-ID
```java
Attachment logo = Attachment.builder().fromFile("C:/temp/logo.png").dispositionInline().build();
message.addContent(new Content("<b>Test</b><br><img src=\"cid:" + logo.getContentId() + "\">", Content.ContentType.HTML));
message.addAttachment(logo);
```

### Setting a Custom Content-ID
```java
message.addAttachment(Attachment.builder().fromFile("C:/temp/logo.png").dispositionInline("logo").build());
message.addContent(new Content("<b>Test</b><br><img src=\"cid:logo\">", Content.ContentType.HTML));
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
