package com.proofpoint.secureemailrelay.mail;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeAdapter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

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

    public static boolean tryDecodeBase64(String base64String) {
        try {
            Base64.getDecoder().decode(base64String);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String encodeFileContent(File file) throws IOException {
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        if (fileBytes.length == 0) {
            throw new IllegalArgumentException("File '" + file.getPath() + "' is empty and cannot be converted to an attachment.");
        }
        return Base64.getEncoder().encodeToString(fileBytes);
    }

    public static InitialStep builder() {
        return new Builder();
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

    public interface InitialStep {
        OptionalStep fromBase64(String base64Content, String filename);

        OptionalStep fromFile(String filePath);

        OptionalStep fromBytes(byte[] data, String filename);
    }

    public interface OptionalStep {
        OptionalStep dispositionAttached();

        OptionalStep dispositionInline();

        OptionalStep dispositionInline(String contentId);

        OptionalStep filename(String filename);

        OptionalStep mimeType(String mimeType);

        Attachment build();
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

    private static class Builder implements InitialStep, OptionalStep {
        private String content;
        private String filename;
        private String mimeType;
        private Disposition disposition;
        private String contentId;


        private Builder() {
            this.dispositionAttached();
        }

        @Override
        public OptionalStep dispositionAttached() {
            this.disposition = Disposition.ATTACHMENT;
            this.contentId = null;
            return this;
        }

        @Override
        public OptionalStep dispositionInline() {
            this.disposition = Disposition.INLINE;
            this.contentId = null;
            return this;
        }

        @Override
        public OptionalStep dispositionInline(String contentId) {
            this.contentId = Objects.requireNonNull(contentId, "ContentId must not be null.");
            this.disposition = Disposition.INLINE;
            return this;
        }

        @Override
        public OptionalStep filename(String filename) {
            this.filename = Objects.requireNonNull(filename, "Filename must not be null.");
            return this;
        }

        @Override
        public OptionalStep mimeType(String mimeType) {
            this.mimeType = Objects.requireNonNull(mimeType, "MimeType must not be null.");
            return this;
        }

        @Override
        public OptionalStep fromFile(String filePath) {
            Objects.requireNonNull(filePath, "File path must not be null.");
            File file = new File(filePath);
            if (!file.exists()) {
                throw new RuntimeException("File not found: '" + filePath + "'.");
            }

            String encodedContent;
            try {
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                if (fileBytes.length == 0) {
                    throw new IllegalArgumentException("File '" + filePath + "' is empty.");
                }
                encodedContent = Base64.getEncoder().encodeToString(fileBytes);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read file: " + filePath, e);
            }
            this.content = encodedContent;
            this.filename = file.getName();
            return this;
        }

        @Override
        public OptionalStep fromBytes(byte[] data, String filename) {
            Objects.requireNonNull(data, "Byte array must not be null.");
            Objects.requireNonNull(filename, "Filename must not be null.");
            this.content = Base64.getEncoder().encodeToString(data);
            this.filename = filename;
            return this;
        }

        @Override
        public OptionalStep fromBase64(String base64Content, String filename) {
            Objects.requireNonNull(base64Content, "Base64 content must not be null.");
            Objects.requireNonNull(filename, "Filename must not be null.");
            tryDecodeBase64(base64Content);
            this.content = base64Content;
            this.filename = filename;
            return this;
        }

        @Override
        public Attachment build() {
            return new Attachment(
                    content,
                    filename,
                    mimeType,
                    disposition,
                    contentId
            );
        }
    }
}
