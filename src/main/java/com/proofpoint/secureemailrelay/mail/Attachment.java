package com.proofpoint.secureemailrelay.mail;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeAdapter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.UUID;

@JsonbNillable
public final class Attachment {
    public static final IMimeMapper MimeTypeMapper = new DefaultMimeMapper();
    private static final Jsonb JSONB = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
    @JsonbProperty("content")
    private final String content;
    @JsonbProperty("disposition")
    @JsonbTypeAdapter(DispositionJsonAdapter.class)
    private final Disposition disposition;
    @JsonbProperty("filename")
    private final String filename;
    @JsonbProperty("id")
    private final String contentId;
    @JsonbProperty("type")
    private final String mimeType;

    private Attachment(String content, String filename, String mimeType, Disposition disposition, String contentId) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null.");
        }

        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null.");
        }

        if (!tryDecodeBase64(content))
            throw new IllegalArgumentException("Content must be a valid Base64-encoded string.");

        if (filename.isBlank()) {
            throw new IllegalArgumentException("Filename cannot be empty or contain only whitespace.");
        }

        if (filename.length() > 1000)
            throw new IllegalArgumentException("Filename must not exceed 1000 characters.");

        if (mimeType != null && mimeType.isBlank()) {
            throw new IllegalArgumentException("MIME type cannot be empty or contain only whitespace.");
        }

        if (mimeType == null)
            mimeType = MimeTypeMapper.getMimeType(filename);

        if (mimeType.isBlank())
            throw new IllegalArgumentException("MIME type must be a valid, non-empty string.");

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

    public static boolean tryDecodeBase64(String base64String) {
        try {
            Base64.getDecoder().decode(base64String);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static String encodeFileContent(File file) throws IOException {
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        if (fileBytes.length == 0) {
            throw new IllegalArgumentException("File '" + file.getPath() + "' is empty and cannot be converted to an attachment.");
        }
        return Base64.getEncoder().encodeToString(fileBytes);
    }

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

    @Override
    public String toString() {
        return JSONB.toJson(this);
    }

    public enum Disposition {
        INLINE("inline"),
        ATTACHMENT("attachment");

        private final String value;

        Disposition(String value) {
            this.value = value.toLowerCase();
        }

        public static Disposition fromString(String str) {
            for (Disposition d : Disposition.values()) {
                if (d.value.equalsIgnoreCase(str)) {
                    return d;
                }
            }
            throw new IllegalArgumentException("Invalid Disposition value: '" + str + "'");
        }

        public String getAttachmentType() {
            return value;
        }
    }

    public static class DispositionJsonAdapter implements JsonbAdapter<Disposition, String> {
        @Override
        public String adaptToJson(Disposition disposition) {
            return disposition.getAttachmentType();
        }

        @Override
        public Disposition adaptFromJson(String value) {
            return Disposition.fromString(value);
        }
    }
}
