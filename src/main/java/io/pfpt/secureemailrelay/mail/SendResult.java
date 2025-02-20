package io.pfpt.secureemailrelay.mail;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class SendResult {
  private final String messageId;
  private final String reason;
  private final String requestId;
  private final HttpResponse<String> httpResponse;
  private final String rawJson;

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
        // Handle JSON parsing failure gracefully
      }
    }

    this.messageId = parsedJson.getOrDefault("message_id", "").toString();
    this.reason = parsedJson.getOrDefault("reason", "").toString();
    this.requestId = parsedJson.getOrDefault("request_id", "").toString();
  }

  public static CompletableFuture<SendResult> createAsync(HttpResponse<String> httpResponse) {
    return CompletableFuture.supplyAsync(() -> new SendResult(httpResponse, httpResponse.body()));
  }

  public String getMessageId() {
    return messageId;
  }

  public String getReason() {
    return reason;
  }

  public String getRequestId() {
    return requestId;
  }

  public HttpResponse<String> getHttpResponse() {
    return httpResponse;
  }

  public String getRawJson() {
    return rawJson;
  }
}
