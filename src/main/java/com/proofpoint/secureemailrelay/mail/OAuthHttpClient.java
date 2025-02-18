package com.proofpoint.secureemailrelay.mail;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

public class OAuthHttpClient implements IHttpClient {
    private final int tokenRefreshOffset;
    private final HttpClient httpClient;
    private final String tokenEndpoint;
    private final String clientId;
    private final String clientSecret;
    private final String scope;
    private final ReentrantLock tokenLock = new ReentrantLock();
    private String accessToken;
    private Instant tokenExpiration;
    private CompletableFuture<Void> tokenRefreshFuture;

    public OAuthHttpClient(String tokenEndpoint, String clientId, String clientSecret, String scope, int tokenRefreshOffset) {
        this(tokenEndpoint, clientId, clientSecret, scope, tokenRefreshOffset, HttpClient.newHttpClient());
    }

    public OAuthHttpClient(String tokenEndpoint, String clientId, String clientSecret, String scope, int tokenRefreshOffset, HttpClient httpClient) {
        this.tokenEndpoint = Objects.requireNonNull(tokenEndpoint, "Token endpoint must not be null.");
        this.clientId = Objects.requireNonNull(clientId, "Client ID must not be null.");
        this.clientSecret = Objects.requireNonNull(clientSecret, "Client Secret must not be null.");
        this.scope = Objects.requireNonNull(scope, "Scope must not be null.");
        this.httpClient = Objects.requireNonNull(httpClient, "HttpClient must not be null.");

        if (tokenRefreshOffset < 0) {
            throw new IllegalArgumentException("Token refresh offset must be a positive number.");
        }
        this.tokenRefreshOffset = tokenRefreshOffset;
    }

    private CompletableFuture<Void> ensureTokenAsync() {
        if (accessToken != null && tokenExpiration != null && Instant.now().isBefore(tokenExpiration)) {
            return CompletableFuture.completedFuture(null); // Use existing valid token
        }

        tokenLock.lock();
        try {
            if (accessToken != null && tokenExpiration != null && Instant.now().isBefore(tokenExpiration)) {
                return CompletableFuture.completedFuture(null); // Use existing valid token
            }

            // If a refresh is already in progress, return the existing future
            if (tokenRefreshFuture != null) {
                return tokenRefreshFuture;
            }

            // Start a new token refresh and store the future
            tokenRefreshFuture = refreshTokenAsync()
                    .whenComplete((result, ex) -> {
                        tokenLock.lock();
                        try {
                            tokenRefreshFuture = null; // Reset the refresh future after completion
                        } finally {
                            tokenLock.unlock();
                        }
                    });

            return tokenRefreshFuture;
        } finally {
            tokenLock.unlock();
        }
    }

    private CompletableFuture<Void> refreshTokenAsync() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenEndpoint))
                .header("User-Agent", "Java-SER-API/1.0")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(BodyPublishers.ofString(
                        "grant_type=client_credentials"
                                + "&client_id=" + clientId
                                + "&client_secret=" + clientSecret
                                + "&scope=" + scope))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::parseTokenResponse);
    }

    private void parseTokenResponse(String responseBody) {
        try (JsonReader reader = Json.createReader(new StringReader(responseBody))) {
            JsonObject json = reader.readObject();
            this.accessToken = json.getString("access_token", null);
            if (this.accessToken == null) {
                throw new IllegalStateException("OAuth token response did not contain an access token.");
            }

            if (json.containsKey("token_expires_date_time")) {
                this.tokenExpiration = Instant.parse(json.getString("token_expires_date_time")).minusSeconds(tokenRefreshOffset);
            } else if (json.containsKey("expires_in")) {
                this.tokenExpiration = Instant.now().plusSeconds(json.getInt("expires_in") - tokenRefreshOffset);
            } else {
                throw new IllegalStateException("OAuth token response is missing expiration details.");
            }
        }
    }

    private HttpRequest.Builder createRequestBuilder(String requestUri, String method, String body) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(requestUri))
                .header("User-Agent", "Java-SER-API/1.0")
                .header("Authorization", "Bearer " + accessToken);

        if (body != null) {
            builder.header("Content-Type", "application/json");
        }

        switch (method) {
            case "GET":
                builder.GET();
                break;
            case "POST":
                builder.POST(BodyPublishers.ofString(body));
                break;
            case "PUT":
                builder.PUT(BodyPublishers.ofString(body));
                break;
            case "DELETE":
                builder.DELETE();
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        return builder;
    }

    @Override
    public CompletableFuture<HttpResponse<String>> getAsync(String requestUri) {
        return ensureTokenAsync().thenCompose(v -> httpClient.sendAsync(createRequestBuilder(requestUri, "GET", null).build(), HttpResponse.BodyHandlers.ofString()));
    }

    @Override
    public CompletableFuture<HttpResponse<String>> postAsync(String requestUri, String content) {
        return ensureTokenAsync().thenCompose(v -> httpClient.sendAsync(createRequestBuilder(requestUri, "POST", content).build(), HttpResponse.BodyHandlers.ofString()));
    }

    @Override
    public CompletableFuture<HttpResponse<String>> putAsync(String requestUri, String content) {
        return ensureTokenAsync().thenCompose(v -> httpClient.sendAsync(createRequestBuilder(requestUri, "PUT", content).build(), HttpResponse.BodyHandlers.ofString()));
    }

    @Override
    public CompletableFuture<HttpResponse<String>> deleteAsync(String requestUri) {
        return ensureTokenAsync().thenCompose(v -> httpClient.sendAsync(createRequestBuilder(requestUri, "DELETE", null).build(), HttpResponse.BodyHandlers.ofString()));
    }

    @Override
    public CompletableFuture<HttpResponse<String>> sendAsync(HttpRequest request) {
        return ensureTokenAsync().thenCompose(v -> httpClient.sendAsync(HttpRequest.newBuilder().header("Authorization", "Bearer " + accessToken).build(), HttpResponse.BodyHandlers.ofString()));
    }
}
