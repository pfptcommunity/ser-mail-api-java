package io.pfpt.secureemailrelay.exceptions;

public class HttpRequestException extends Exception {
  public HttpRequestException(String message) {
    super(message);
  }

  public HttpRequestException(String message, Throwable cause) {
    super(message, cause);
  }
}
