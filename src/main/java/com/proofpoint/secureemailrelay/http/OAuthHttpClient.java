package com.proofpoint.secureemailrelay.http;

import com.proofpoint.secureemailrelay.exceptions.HttpRequestException;
import com.proofpoint.secureemailrelay.exceptions.HttpTokenRefreshException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;


public class OAuthHttpClient {
    private final HttpClient httpClient;
    private final String tokenEndpoint;
    private final String clientId;
    private final String clientSecret;
    private final String scope;
    private final int tokenRefreshOffset;
    private final int maxRetries = 3;
    private final long backoffMillis = 1000;

    private final AtomicReference<String> accessToken = new AtomicReference<>();
    private final AtomicReference<Instant> tokenExpiration = new AtomicReference<>();
    private final ReentrantLock refreshLock = new ReentrantLock();

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

    /**
     * Ensures that a valid token is available before making a request.
     */
    private void ensureToken() throws HttpTokenRefreshException {
        String token = accessToken.get();
        Instant expiration = tokenExpiration.get();

        // If the token is still valid, return it immediately
        if (token != null && expiration != null && Instant.now().isBefore(expiration)) {
            return;
        }

        // Lock only when a refresh is needed (threads waiting here for the update)
        refreshLock.lock();
        try {
            // Double-check inside the lock to prevent redundant refreshes
            token = accessToken.get();
            expiration = tokenExpiration.get();

            if (token != null && expiration != null && Instant.now().isBefore(expiration)) {
                return;
            }

            refreshToken();
        } finally {
            refreshLock.unlock();
        }
    }

    /**
     * Refreshes the OAuth token synchronously.
     */
    private void refreshToken() throws HttpTokenRefreshException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenEndpoint))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "grant_type=client_credentials"
                                + "&client_id=" + clientId
                                + "&client_secret=" + clientSecret
                                + "&scope=" + scope))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            try (JsonReader reader = Json.createReader(new StringReader(response.body()))) {
                JsonObject json = reader.readObject();
                String newAccessToken = json.getString("access_token", null);
                if (newAccessToken == null) {
                    throw new HttpTokenRefreshException("OAuth token response did not contain an access token.");
                }

                Instant newExpiration;
                if (json.containsKey("token_expires_date_time")) {
                    newExpiration = Instant.parse(json.getString("token_expires_date_time")).minusSeconds(tokenRefreshOffset);
                } else if (json.containsKey("expires_in")) {
                    newExpiration = Instant.now().plusSeconds(json.getInt("expires_in") - tokenRefreshOffset);
                } else {
                    throw new HttpTokenRefreshException("OAuth token response is missing expiration details.");
                }

                // Update the token atomically
                accessToken.set(newAccessToken);
                tokenExpiration.set(newExpiration);
            }
        } catch (IOException e) {
            throw new HttpTokenRefreshException("Failed to send HTTP request for OAuth token.", e);
        } catch (JsonParsingException e) {
            throw new HttpTokenRefreshException("Failed to parse JSON response for OAuth token.", e);
        } catch (Exception e) {
            throw new HttpTokenRefreshException("Unexpected error during OAuth token refresh.", e);
        }
    }


    /**
     * Provides 1-to-1 usage with HttpClient send.
     */
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
        try {
            ensureToken();
        } catch (HttpTokenRefreshException e) {
            CompletableFuture<HttpResponse<T>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new HttpRequestException("Failed to refresh token before sending request", e));
            return failedFuture;
        }

        HttpRequest.Builder authorizedRequestBuilder = copyRequest(request);
        authorizedRequestBuilder.header("Authorization", "Bearer " + accessToken.get());

        return httpClient.sendAsync(authorizedRequestBuilder.build(), responseBodyHandler)
                .handle((response, ex) -> {
                    if (ex != null) {
                        String errorMessage = String.format(
                                "Asynchronous request failed: %s %s",
                                request.method(), request.uri()
                        );
                        CompletableFuture<HttpResponse<T>> failedFuture = new CompletableFuture<>();
                        failedFuture.completeExceptionally(new HttpRequestException(errorMessage, ex));
                        return failedFuture.join(); // Join forces this CompletableFuture to throw the exception
                    }
                    return response;
                });
    }


    /**
     * Clone the HTTPRequest object.
     */
    private HttpRequest.Builder copyRequest(HttpRequest original) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(original.uri()).method(original.method(), original.bodyPublisher().orElse(HttpRequest.BodyPublishers.noBody()));
        original.headers().map().forEach((key, values) -> {
            if (!key.equalsIgnoreCase("Authorization")) {
                values.forEach(value -> builder.header(key, value));
            }
        });
        original.timeout().ifPresent(builder::timeout);
        original.version().ifPresent(builder::version);
        builder.expectContinue(original.expectContinue());
        return builder;
    }
}
