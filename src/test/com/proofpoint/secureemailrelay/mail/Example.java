package test.com.proofpoint.secureemailrelay.mail;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import com.proofpoint.secureemailrelay.mail.Attachment;
import com.proofpoint.secureemailrelay.mail.Attachment.Disposition;
import com.proofpoint.secureemailrelay.mail.Attachment;
import com.proofpoint.secureemailrelay.mail.Content;

public class Example {
    public static void main(String[] args) {
        Jsonb jsonb = JsonbBuilder.create();
        Attachment attachment = Attachment.fromBase64( "VGhpcyBpcyBhIHRlc3Qh", "test.txt", "text/plain", Disposition.INLINE, null);
        Content content = new Content("This is my message body", Content.ContentType.TEXT);
        System.out.println(attachment.toString());
        System.out.println(content.toString());
    }
}
