package io.pfpt.ser.exceptions;

/**
 * Custom exception class for handling errors related to HTTP requests. This exception is thrown
 * when an HTTP request fails due to network issues, invalid responses, or other request-specific
 * problems.
 */
public class HttpRequestException extends Exception {

  /**
   * Constructs a new HttpRequestException with the specified error message.
   *
   * @param message the detail message explaining the reason for the exception
   */
  public HttpRequestException(String message) {
    super(message); // Passes the message to the parent Exception class
  }

  /**
   * Constructs a new HttpRequestException with the specified error message and cause.
   *
   * @param message the detail message explaining the reason for the exception
   * @param cause the underlying cause of the exception (e.g., a nested exception)
   */
  public HttpRequestException(String message, Throwable cause) {
    super(message, cause); // Passes the message and cause to the parent Exception class
  }
}
