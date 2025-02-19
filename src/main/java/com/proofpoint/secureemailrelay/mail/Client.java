package com.proofpoint.secureemailrelay.mail;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class Client {
    private static final Jsonb JSONB = JsonbBuilder.create();
    private final OAuthHttpClient httpClient;

    public Client(String clientId, String clientSecret) {
        this(clientId, clientSecret, HttpClient.newHttpClient());
    }

    public Client(String clientId, String clientSecret, HttpClient httpClient) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("Client ID must not be null or empty.");
        }
        if (clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalArgumentException("Client Secret must not be null or empty.");
        }
        Objects.requireNonNull(httpClient, "HttpClient must not be null.");

        try {
            this.httpClient = new OAuthHttpClient(
                    "https://mail.ser.proofpoint.com/v1/token",
                    clientId,
                    clientSecret,
                    "",
                    300, // Refresh 5 minutes early
                    httpClient
            );

        } catch (Exception ex) {
            throw new IllegalStateException("Failed to initialize the Proofpoint SER API client.", ex);
        }
    }

    public CompletableFuture<SendResult> send(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message must not be null.");
        }

        String json;
        try {
            json = JSONB.toJson(message);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize the message to JSON.", ex);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://mail.ser.proofpoint.com/v1/send"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(SendResult::createAsync)
                .exceptionally(ex -> {
                    throw new IllegalStateException("Failed to send the email request due to an HTTP error.", ex);
                });
    }
}
