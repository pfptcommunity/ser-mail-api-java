package com.proofpoint.secureemailrelay.exceptions;

public class HttpTokenRefreshException extends Exception {
  public HttpTokenRefreshException(String message) {
    super(message);
  }

  public HttpTokenRefreshException(String message, Throwable cause) {
    super(message, cause);
  }
}
