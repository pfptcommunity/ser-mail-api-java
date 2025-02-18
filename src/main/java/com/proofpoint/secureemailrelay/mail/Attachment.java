package com.proofpoint.secureemailrelay.mail;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbProperty;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.UUID;

@JsonbNillable
public class Attachment {

    public enum Disposition {
        INLINE("inline"),
        ATTACHMENT("attachment");

        private final String value;

        Disposition(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Disposition fromString(String str) {
            for (Disposition d : Disposition.values()) {
                if (d.value.equalsIgnoreCase(str)) {
                    return d;
                }
            }
            throw new IllegalArgumentException("Invalid Disposition value: '" + str + "'.");
        }
    }

    @JsonbProperty("content")
    private final String content;

    @JsonbProperty("disposition")
    private final Disposition disposition;

    @JsonbProperty("filename")
    private final String filename;

    @JsonbProperty("id")
    private final String contentId;

    @JsonbProperty("type")
    private final String mimeType;

    public String getContent() {
        return content;
    }

    public Disposition getDisposition() {
        return disposition;
    }

    public String getFilename() {
        return filename;
    }

    public String getContentId() {
        return contentId;
    }

    public String getMimeType() {
        return mimeType;
    }

    private Attachment(String content, String filename, String mimeType, Disposition disposition, String contentId) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be null or empty.");
        }
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename cannot be null or empty.");
        }
        if (mimeType == null || mimeType.isBlank()) {
            throw new IllegalArgumentException("MIME type cannot be null or empty.");
        }
        if (disposition == null) {
            throw new IllegalArgumentException("Disposition cannot be null.");
        }

        this.content = content;
        this.filename = filename;
        this.mimeType = mimeType;
        this.disposition = disposition;
        this.contentId = (contentId == null || contentId.isBlank())
                ? (disposition == Disposition.INLINE ? UUID.randomUUID().toString() : null)
                : contentId;
    }

    public static Attachment fromBase64(String base64Content, String filename, String mimeType, Disposition disposition, String contentId) {
        return new Attachment(base64Content, filename, mimeType, disposition, contentId);
    }

    public static Attachment fromFile(String filePath, Disposition disposition, String contentId, String filename, String mimeType) throws IOException {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("File path cannot be null or empty.");
        }
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: '" + filePath + "'.");
        }

        String encodedContent = encodeFileContent(file);
        return new Attachment(encodedContent, filename != null ? filename : file.getName(), mimeType, disposition, contentId);
    }

    public static Attachment fromBytes(byte[] data, String filename, String mimeType, Disposition disposition) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Byte array must not be null or empty.");
        }
        return new Attachment(Base64.getEncoder().encodeToString(data), filename, mimeType, disposition, null);
    }

    private static String encodeFileContent(File file) throws IOException {
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        if (fileBytes.length == 0) {
            throw new IllegalArgumentException("File '" + file.getPath() + "' is empty and cannot be converted to an attachment.");
        }
        return Base64.getEncoder().encodeToString(fileBytes);
    }

    @Override
    public String toString() {
        try {
            Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
            return jsonb.toJson(this);
        } catch (Exception e) {
            return "{}";
        }
    }
}
