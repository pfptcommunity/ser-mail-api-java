package io.pfpt.secureemailrelay.mail;

import java.util.Objects;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTypeAdapter;

public final class Content {

    private static final Jsonb JSONB = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
    @JsonbProperty("body")
    private String body;
    @JsonbProperty("type")
    @JsonbTypeAdapter(Content.ContentTypeJsonAdapter.class)
    private ContentType contentType;

    public Content(String body, ContentType contentType) {
        this.setBody(body);
        this.setContentType(contentType);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = Objects.requireNonNull(body, "Body must not be null.");
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = Objects.requireNonNull(contentType, "Content type must not be null.");
    }

    @Override
    public String toString() {
        return JSONB.toJson(this);
    }

    public enum ContentType {
        TEXT("text/plain"),
        HTML("text/html");

        private final String mimeType;

        ContentType(String mimeType) {
            this.mimeType = mimeType;
        }

        public static ContentType fromString(String str) {
            Objects.requireNonNull(str, "Content type string must not be null.");
            try {
                return ContentType.valueOf(str.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid content type value: '" + str + "'. Expected 'TEXT' or 'HTML'.");
            }
        }

        public String getMimeType() {
            return mimeType;
        }
    }

    public static class ContentTypeJsonAdapter implements JsonbAdapter<ContentType, String> {
        @Override
        public String adaptToJson(ContentType contentType) {
            return contentType.getMimeType();
        }

        @Override
        public ContentType adaptFromJson(String value) {
            return ContentType.fromString(value);
        }
    }
}
