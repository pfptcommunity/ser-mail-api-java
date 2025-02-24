package io.pfpt.ser.http;

import io.pfpt.ser.exceptions.HttpRequestException;
import io.pfpt.ser.exceptions.HttpTokenRefreshException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.stream.JsonParsingException;

/**
 * A client for making HTTP requests with OAuth 2.0 authentication.
 * This class manages token retrieval and refresh operations, ensuring that
 * requests are sent with a valid access token. It supports asynchronous requests
 * and handles token expiration gracefully using thread-safe mechanisms.
 */
public class OAuthHttpClient {
    // The underlying HTTP client used to send requests
    private final HttpClient httpClient;
    // The OAuth token endpoint URL for refreshing tokens
    private final String tokenEndpoint;
    // The client ID for OAuth authentication
    private final String clientId;
    // The client secret for OAuth authentication
    private final String clientSecret;
    // The scope defining the access level requested for the token
    private final String scope;
    // The offset (in seconds) before token expiration to trigger a refresh
    private final int tokenRefreshOffset;

    // Thread-safe storage for the current access token
    private final AtomicReference<String> accessToken = new AtomicReference<>();
    // Thread-safe storage for the token's expiration time
    private final AtomicReference<Instant> tokenExpiration = new AtomicReference<>();
    // Lock to synchronize token refresh operations across threads
    private final ReentrantLock refreshLock = new ReentrantLock();

    /**
     * Constructs an OAuthHttpClient with default HttpClient and specified OAuth parameters.
     *
     * @param tokenEndpoint the URL of the OAuth token endpoint
     * @param clientId the OAuth client ID
     * @param clientSecret the OAuth client secret
     * @param scope the requested scope for the token
     * @param tokenRefreshOffset seconds before expiration to refresh the token
     */
    public OAuthHttpClient(String tokenEndpoint, String clientId, String clientSecret, String scope, int tokenRefreshOffset) {
        this(tokenEndpoint, clientId, clientSecret, scope, tokenRefreshOffset, HttpClient.newHttpClient());
    }

    /**
     * Constructs an OAuthHttpClient with a custom HttpClient and specified OAuth parameters.
     *
     * @param tokenEndpoint the URL of the OAuth token endpoint
     * @param clientId the OAuth client ID
     * @param clientSecret the OAuth client secret
     * @param scope the requested scope for the token
     * @param tokenRefreshOffset seconds before expiration to refresh the token
     * @param httpClient the custom HttpClient instance to use for requests
     */
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
     * Checks the current token's validity and refreshes it if necessary, using
     * a lock to prevent concurrent refreshes.
     *
     * @throws HttpTokenRefreshException if token refresh fails
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
     * Sends a request to the token endpoint to obtain a new access token and
     * updates the stored token and expiration time.
     *
     * @throws HttpTokenRefreshException if the refresh operation fails due to
     * network issues, invalid responses, or parsing errors
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
     * Sends an HTTP request asynchronously and returns a CompletableFuture with the response.
     * This method handles OAuth authentication and delegates the request to an underlying HTTP client.
     *
     * @param <T> the type of the response body, determined by the provided responseBodyHandler
     * @param request the HTTP request to send, including headers and body if applicable
     * @param responseBodyHandler the handler to convert the response body into the desired type T
     * @return a CompletableFuture that completes with the HTTP response containing the processed body
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
                .handle((response, throwable) -> {
                    if (throwable != null) {
                        String errorMessage = String.format(
                                "Asynchronous request failed: %s %s",
                                request.method(), request.uri()
                        );
                        throw new CompletionException(errorMessage, throwable);
                    }
                    return response;
                });
    }

    /**
     * Creates a new HttpRequest.Builder by copying an existing HttpRequest.
     * Preserves the original request's properties except for the Authorization header,
     * which is managed separately.
     *
     * @param original the HttpRequest to clone
     * @return a new HttpRequest.Builder with the copied properties
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