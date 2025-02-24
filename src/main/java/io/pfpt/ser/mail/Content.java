package io.pfpt.ser.mail;

import java.util.Objects;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTypeAdapter;

/**
 * Represents the content of an email, including its body and content type.
 * This class supports plain text or HTML content and provides JSON serialization
 * for integration with mail-related operations.
 */
public final class Content {

    // JSON-B instance configured for pretty-printed serialization
    private static final Jsonb JSONB = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
    // The body of the email content
    @JsonbProperty("body")
    private String body;
    // The type of content (e.g., plain text or HTML)
    @JsonbProperty("type")
    @JsonbTypeAdapter(Content.ContentTypeJsonAdapter.class)
    private ContentType contentType;

    /**
     * Constructs a Content instance with the specified body and content type.
     *
     * @param body the content body (e.g., text or HTML markup)
     * @param contentType the type of content (TEXT or HTML)
     */
    public Content(String body, ContentType contentType) {
        this.setBody(body);
        this.setContentType(contentType);
    }

    /**
     * Retrieves the body of the content.
     *
     * @return the content body as a string
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the body of the content.
     *
     * @param body the content body to set
     * @throws NullPointerException if the body is null
     */
    public void setBody(String body) {
        this.body = Objects.requireNonNull(body, "Body must not be null.");
    }

    /**
     * Retrieves the content type.
     *
     * @return the content type (TEXT or HTML)
     */
    public ContentType getContentType() {
        return contentType;
    }

    /**
     * Sets the content type.
     *
     * @param contentType the content type to set (TEXT or HTML)
     * @throws NullPointerException if the content type is null
     */
    public void setContentType(ContentType contentType) {
        this.contentType = Objects.requireNonNull(contentType, "Content type must not be null.");
    }

    /**
     * Serializes the content to a JSON string using JSON-B.
     *
     * @return a pretty-printed JSON representation of the content
     */
    @Override
    public String toString() {
        return JSONB.toJson(this);
    }

    /**
     * Enum representing the type of email content.
     * Supports plain text (TEXT) and HTML (HTML) with corresponding MIME types.
     */
    public enum ContentType {
        TEXT("text/plain"),  // Plain text content
        HTML("text/html");   // HTML-formatted content

        // The MIME type associated with the content type
        private final String mimeType;

        /**
         * Constructs a ContentType with the specified MIME type.
         *
         * @param mimeType the MIME type string
         */
        ContentType(String mimeType) {
            this.mimeType = mimeType;
        }

        /**
         * Parses a string into a ContentType value.
         *
         * @param str the string to parse (e.g., "TEXT" or "HTML")
         * @return the corresponding ContentType
         * @throws NullPointerException if the string is null
         * @throws IllegalArgumentException if the string is invalid
         */
        public static ContentType fromString(String str) {
            Objects.requireNonNull(str, "Content type string must not be null.");
            try {
                return ContentType.valueOf(str.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid content type value: '" + str + "'. Expected 'TEXT' or 'HTML'.");
            }
        }

        /**
         * Retrieves the MIME type of the content.
         *
         * @return the MIME type string (e.g., "text/plain" or "text/html")
         */
        public String getMimeType() {
            return mimeType;
        }
    }

    /**
     * JSON-B adapter for serializing and deserializing ContentType enum.
     * Converts between enum values and their MIME type strings.
     */
    public static class ContentTypeJsonAdapter implements JsonbAdapter<ContentType, String> {
        /**
         * Converts a ContentType to its MIME type string for JSON serialization.
         *
         * @param contentType the ContentType to serialize
         * @return the MIME type string
         */
        @Override
        public String adaptToJson(ContentType contentType) {
            return contentType.getMimeType();
        }

        /**
         * Converts a MIME type string from JSON into a ContentType.
         *
         * @param value the MIME type string to deserialize
         * @return the corresponding ContentType
         * @throws IllegalArgumentException if the value is invalid
         */
        @Override
        public ContentType adaptFromJson(String value) {
            return ContentType.fromString(value);
        }
    }
}