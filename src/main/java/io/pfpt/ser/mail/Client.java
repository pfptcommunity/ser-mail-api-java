package io.pfpt.ser.mail;

import io.pfpt.ser.http.OAuthHttpClient;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * A client for sending emails via the Proofpoint SER API. This class uses an OAuth-authenticated
 * HTTP client to send messages asynchronously, handling JSON serialization and API communication.
 */
public final class Client {
  // JSON-B instance for serializing messages to JSON
  private static final Jsonb JSONB = JsonbBuilder.create();
  // The OAuth-authenticated HTTP client for making API requests
  private final OAuthHttpClient httpClient;
  // The API service region
  private final Region region;

  /**
   * Constructs a Client with the specified client ID and secret, using a default HttpClient.
   *
   * @param clientId the OAuth client ID for authentication
   * @param clientSecret the OAuth client secret for authentication
   */
  public Client(String clientId, String clientSecret) {
    this(clientId, clientSecret, HttpClient.newHttpClient(), Region.US);
  }

  /**
   * Constructs a Client with the specified client ID and secret, using a default HttpClient.
   *
   * @param clientId the OAuth client ID for authentication
   * @param clientSecret the OAuth client secret for authentication
   * @param region the API service region
   */
  public Client(String clientId, String clientSecret, Region region) {
    this(clientId, clientSecret, HttpClient.newHttpClient(), region);
  }

  /**
   * Constructs a Client with the specified client ID, secret, and custom HttpClient.
   *
   * @param clientId the OAuth client ID for authentication
   * @param clientSecret the OAuth client secret for authentication
   * @param httpClient the custom HttpClient to use for HTTP requests
   * @throws IllegalArgumentException if clientId or clientSecret is null or blank
   * @throws NullPointerException if httpClient is null
   * @throws IllegalStateException if OAuthHttpClient initialization fails
   */
  public Client(String clientId, String clientSecret, HttpClient httpClient) {
    this(clientId, clientSecret, httpClient, Region.US);
  }

  /**
   * Constructs a Client with the specified client ID, secret, HttpClient, and region.
   *
   * @param clientId the OAuth client ID for authentication
   * @param clientSecret the OAuth client secret for authentication
   * @param httpClient the custom HttpClient to use for HTTP requests
   * @param region the API service region
   * @throws IllegalArgumentException if clientId or clientSecret is null or blank
   * @throws NullPointerException if httpClient or region is null
   * @throws IllegalStateException if OAuthHttpClient initialization fails
   */
  public Client(String clientId, String clientSecret, HttpClient httpClient, Region region) {
    if (clientId == null || clientId.isBlank()) {
      throw new IllegalArgumentException("Client ID must not be null or empty.");
    }
    if (clientSecret == null || clientSecret.isBlank()) {
      throw new IllegalArgumentException("Client Secret must not be null or empty.");
    }
    Objects.requireNonNull(httpClient, "HttpClient must not be null.");
    Objects.requireNonNull(region, "Region must not be null.");

    try {
      this.httpClient = new OAuthHttpClient(
              String.format("https://%s/v1/token", region.getString()), // Dynamic token endpoint
              clientId,
              clientSecret,
              "", // No specific scope defined
              300, // Refresh token 5 minutes before expiration
              httpClient);
      this.region = region;
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to initialize the Proofpoint SER API client.", ex);
    }
  }

  /**
   * Sends an email message asynchronously via the Proofpoint SER API. Serializes the message to
   * JSON and submits it to the API endpoint, returning a future result.
   *
   * @param message the Message object to send
   * @return a CompletableFuture resolving to a SendResult containing the outcome of the send operation
   * @throws IllegalArgumentException if the message is null
   * @throws IllegalStateException if JSON serialization fails
   */
  public CompletableFuture<SendResult> send(Message message) {
    if (message == null) {
      throw new IllegalArgumentException("Message must not be null.");
    }

    String json;
    try {
      json = JSONB.toJson(message); // Convert message to JSON string
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to serialize the message to JSON.", ex);
    }

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(String.format("https://%s/v1/send", region.getString()))) // Dynamic send endpoint
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
            .build();

    return httpClient
            .sendAsync(request, HttpResponse.BodyHandlers.ofString()) // Send request asynchronously
            .thenCompose(SendResult::createAsync); // Transform response into SendResult
  }
}