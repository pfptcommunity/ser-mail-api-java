package com.proofpoint.secureemailrelay.mail;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public interface IHttpClient {
    CompletableFuture<HttpResponse<String>> getAsync(String requestUri);

    CompletableFuture<HttpResponse<String>> postAsync(String requestUri, String content);

    CompletableFuture<HttpResponse<String>> putAsync(String requestUri, String content);

    CompletableFuture<HttpResponse<String>> deleteAsync(String requestUri);

    CompletableFuture<HttpResponse<String>> sendAsync(HttpRequest request);
}
