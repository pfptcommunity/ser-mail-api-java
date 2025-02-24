package io.pfpt.ser.exceptions;

/**
 * Custom exception class for handling errors during HTTP token refresh operations. This exception
 * is thrown when a token refresh request fails, such as due to expired credentials, server
 * unavailability, or authentication issues.
 */
public class HttpTokenRefreshException extends Exception {

  /**
   * Constructs a new HttpTokenRefreshException with the specified error message.
   *
   * @param message the detail message explaining the reason for the exception
   */
  public HttpTokenRefreshException(String message) {
    super(message); // Passes the message to the parent Exception class
  }

  /**
   * Constructs a new HttpTokenRefreshException with the specified error message and cause.
   *
   * @param message the detail message explaining the reason for the exception
   * @param cause the underlying cause of the exception (e.g., a nested exception)
   */
  public HttpTokenRefreshException(String message, Throwable cause) {
    super(message, cause); // Passes the message and cause to the parent Exception class
  }
}
