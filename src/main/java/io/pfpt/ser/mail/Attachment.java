package io.pfpt.ser.mail;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a file attachment for an email, encapsulating its content, metadata, and disposition.
 * This class supports creating attachments from various sources (Base64 strings, files, or byte arrays)
 * and ensures proper validation and serialization for mail-related operations.
 */
public final class Attachment {

    // Default MIME type mapper for determining file content types
    public static final IMimeMapper MimeTypeMapper = new DefaultMimeMapper();
    // JSON-B instance configured for pretty-printed serialization
    private static final Jsonb JSONB = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
    // Base64-encoded content of the attachment
    @JsonbProperty("content")
    private final String content;
    // Disposition indicating how the attachment should be handled (inline or attachment)
    @JsonbProperty("disposition")
    @JsonbTypeAdapter(DispositionJsonAdapter.class)
    private final Disposition disposition;
    // Name of the file associated with the attachment
    @JsonbProperty("filename")
    private final String filename;
    // Unique identifier for inline attachments (null for regular attachments)
    @JsonbProperty("id")
    private final String contentId;
    // MIME type of the attachment content
    @JsonbProperty("type")
    private final String mimeType;

    /**
     * Private constructor to enforce creation via builder pattern.
     * Validates all parameters and sets defaults where applicable.
     *
     * @param content Base64-encoded content of the attachment
     * @param filename name of the file
     * @param mimeType MIME type of the content (or inferred if null)
     * @param disposition how the attachment should be displayed
     * @param contentId unique ID for inline attachments (optional)
     */
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

    /**
     * Attempts to decode a Base64 string to validate its format.
     *
     * @param base64String the string to validate
     * @return true if the string is valid Base64, false otherwise
     */
    public static boolean tryDecodeBase64(String base64String) {
        try {
            Base64.getDecoder().decode(base64String);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Encodes a file's content into a Base64 string.
     *
     * @param file the file to encode
     * @return the Base64-encoded string of the file content
     * @throws IOException if reading the file fails
     * @throws IllegalArgumentException if the file is empty
     */
    public static String encodeFileContent(File file) throws IOException {
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        if (fileBytes.length == 0) {
            throw new IllegalArgumentException("File '" + file.getPath() + "' is empty and cannot be converted to an attachment.");
        }
        return Base64.getEncoder().encodeToString(fileBytes);
    }

    /**
     * Initiates the builder pattern for creating an Attachment instance.
     *
     * @return the initial step of the builder
     */
    public static InitialStep builder() {
        return new Builder();
    }

    /** @return the Base64-encoded content of the attachment */
    public String getContent() {
        return content;
    }

    /** @return the disposition of the attachment (inline or attachment) */
    public Disposition getDisposition() {
        return disposition;
    }

    /** @return the filename of the attachment */
    public String getFilename() {
        return filename;
    }

    /** @return the content ID (used for inline attachments), or null if not applicable */
    public String getContentId() {
        return contentId;
    }

    /** @return the MIME type of the attachment content */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Serializes the attachment to a JSON string using JSON-B.
     *
     * @return a pretty-printed JSON representation of the attachment
     */
    @Override
    public String toString() {
        return JSONB.toJson(this);
    }

    /**
     * Enum representing the disposition of an attachment (inline or regular attachment).
     */
    public enum Disposition {
        INLINE("inline"),    // Displayed within the email body
        ATTACHMENT("attachment"); // Presented as a downloadable file

        private final String value;

        Disposition(String value) {
            this.value = value.toLowerCase();
        }

        /**
         * Parses a string into a Disposition value.
         *
         * @param str the string to parse
         * @return the corresponding Disposition
         * @throws IllegalArgumentException if the string is invalid
         */
        public static Disposition fromString(String str) {
            for (Disposition d : Disposition.values()) {
                if (d.value.equalsIgnoreCase(str)) {
                    return d;
                }
            }
            throw new IllegalArgumentException("Invalid Disposition value: '" + str + "'");
        }

        /** @return the string representation of the disposition */
        public String getAttachmentType() {
            return value;
        }
    }

    /** Interface for the initial step of the builder pattern, defining content sources. */
    public interface InitialStep {
        /** Starts building from a Base64 string and filename */
        OptionalStep fromBase64(String base64Content, String filename);

        /** Starts building from a file path */
        OptionalStep fromFile(String filePath);

        /** Starts building from a byte array and filename */
        OptionalStep fromBytes(byte[] data, String filename);
    }

    /** Interface for optional configuration steps in the builder pattern. */
    public interface OptionalStep {
        /** Sets disposition to ATTACHMENT */
        OptionalStep dispositionAttached();

        /** Sets disposition to INLINE with a random content ID */
        OptionalStep dispositionInline();

        /** Sets disposition to INLINE with a specified content ID */
        OptionalStep dispositionInline(String contentId);

        /** Overrides the filename */
        OptionalStep filename(String filename);

        /** Overrides the MIME type */
        OptionalStep mimeType(String mimeType);

        /** Builds the final Attachment instance */
        Attachment build();
    }

    /** JSON-B adapter for serializing/deserializing Disposition enum. */
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

    /** Private builder class implementing the builder pattern for Attachment creation. */
    private static class Builder implements InitialStep, OptionalStep {
        private String content;
        private String filename;
        private String mimeType;
        private Disposition disposition;
        private String contentId;

        private Builder() {
            this.dispositionAttached(); // Default to ATTACHMENT
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
            this.contentId = null; // Will be set to random UUID in constructor
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

            if (filePath.isEmpty())
                throw new IllegalArgumentException("File path must not be empty.");

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