package io.pfpt.ser.mail;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Represents the result of sending an email, encapsulating response details from an HTTP request.
 * This class parses the HTTP response body (assumed to be JSON) to extract key information like
 * message ID, reason, and request ID, while also retaining the raw response data.
 */
public final class SendResult {
  // Unique identifier of the sent message, extracted from the JSON response
  private final String messageId;
  // Reason for the result (e.g., failure explanation), extracted from the JSON response
  private final String reason;
  // Unique identifier of the request, extracted from the JSON response
  private final String requestId;
  // The full HTTP response object from the send operation
  private final HttpResponse<String> httpResponse;
  // The raw JSON string from the HTTP response body
  private final String rawJson;

  /**
   * Private constructor to create a SendResult from an HTTP response and its raw JSON body. Parses
   * the JSON to populate fields, with fallback to empty strings if parsing fails.
   *
   * @param httpResponse the HTTP response from the send operation
   * @param rawJson the raw JSON string from the response body
   * @throws NullPointerException if httpResponse is null
   */
  private SendResult(HttpResponse<String> httpResponse, String rawJson) {
    this.httpResponse = Objects.requireNonNull(httpResponse, "HTTP response must not be null.");
    this.rawJson = rawJson != null ? rawJson : "";

    Map<String, Object> parsedJson = new HashMap<>();
    if (!this.rawJson.isBlank()) {
      try (Jsonb jsonb = JsonbBuilder.create()) {
        parsedJson =
            jsonb.fromJson(
                this.rawJson, new HashMap<String, Object>() {}.getClass().getGenericSuperclass());
      } catch (Exception e) {
        // Handle JSON parsing failure gracefully—fields default to empty strings
      }
    }

    this.messageId = parsedJson.getOrDefault("message_id", "").toString();
    this.reason = parsedJson.getOrDefault("reason", "").toString();
    this.requestId = parsedJson.getOrDefault("request_id", "").toString();
  }

  /**
   * Creates a SendResult asynchronously from an HTTP response. Wraps the construction in a
   * CompletableFuture for non-blocking execution.
   *
   * @param httpResponse the HTTP response from the send operation
   * @return a CompletableFuture resolving to a SendResult instance
   */
  public static CompletableFuture<SendResult> createAsync(HttpResponse<String> httpResponse) {
    return CompletableFuture.supplyAsync(() -> new SendResult(httpResponse, httpResponse.body()));
  }

  /**
   * Retrieves the message ID from the send result.
   *
   * @return the message ID, or an empty string if not present in the response
   */
  public String getMessageId() {
    return messageId;
  }

  /**
   * Retrieves the reason for the send result.
   *
   * @return the reason, or an empty string if not present in the response
   */
  public String getReason() {
    return reason;
  }

  /**
   * Retrieves the request ID from the send result.
   *
   * @return the request ID, or an empty string if not present in the response
   */
  public String getRequestId() {
    return requestId;
  }

  /**
   * Retrieves the full HTTP response from the send operation.
   *
   * @return the HttpResponse object containing status, headers, and body
   */
  public HttpResponse<String> getHttpResponse() {
    return httpResponse;
  }

  /**
   * Retrieves the raw JSON string from the HTTP response body.
   *
   * @return the raw JSON, or an empty string if none was provided
   */
  public String getRawJson() {
    return rawJson;
  }
}
