package com.proofpoint.secureemailrelay.mail;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbProperty;
import java.util.Objects;

public final class Content {

    public enum ContentType {
        TEXT("text/plain"),
        HTML("text/html");

        private final String mimeType;

        ContentType(String mimeType) {
            this.mimeType = mimeType;
        }

        public String getMimeType() {
            return mimeType;
        }

        public static ContentType fromString(String str) {
            switch (str.toLowerCase()) {
                case "text":
                    return TEXT;
                case "html":
                    return HTML;
                default:
                    throw new IllegalArgumentException("Invalid ContentType value: '" + str + "'.");
            }
        }
    }

    @JsonbProperty("body")
    private String body;

    @JsonbProperty("type")
    private final ContentType contentType;

    public Content(String body, ContentType contentType) {
        this.body = Objects.requireNonNull(body, "Body must not be null.");
        if (body.isBlank()) {
            throw new IllegalArgumentException("Body must not be empty or contain only whitespace.");
        }
        this.contentType = Objects.requireNonNull(contentType, "ContentType must not be null.");
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = Objects.requireNonNull(body, "Body must not be null.");
        if (body.isBlank()) {
            throw new IllegalArgumentException("Body must not be empty or contain only whitespace.");
        }
    }

    public ContentType getContentType() {
        return contentType;
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
