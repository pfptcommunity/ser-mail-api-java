package io.pfpt.ser.mail;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbProperty;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a mail user with an email address and an optional name. This class enforces email
 * validation and provides JSON serialization for mail-related operations. Instances of this class
 * are immutable once constructed.
 */
public final class MailUser {

  // Regular expression pattern for validating email addresses per RFC 5322
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile(
          "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

  // JSON-B instance configured for pretty-printed serialization
  private static final Jsonb JSONB = JsonbBuilder.create(new JsonbConfig().withFormatting(true));

  // The email address of the mail user
  @JsonbProperty("email")
  private final String email;

  // The optional name of the mail user
  @JsonbProperty("name")
  private final String name;

  /**
   * Constructs a MailUser with the specified email address and no name.
   *
   * @param email the email address of the user
   */
  public MailUser(String email) {
    this(email, null);
  }

  /**
   * Constructs a MailUser with the specified email address and name.
   *
   * @param email the email address of the user
   * @param name the optional name of the user (can be null)
   */
  public MailUser(String email, String name) {
    this.email = validateAndSetEmail(email);
    this.name = name; // Name can be null, no further validation needed
  }

  /**
   * Validates an email address against the predefined pattern.
   *
   * @param email the email address to validate
   * @throws IllegalArgumentException if the email does not match the expected format
   */
  private static void validateEmail(String email) {
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new IllegalArgumentException("Invalid email format: '" + email + "'.");
    }
  }

  /**
   * Validates and returns the email address for assignment.
   *
   * @param email the email address to validate
   * @return the validated email address
   * @throws NullPointerException if email is null
   * @throws IllegalArgumentException if email is blank or invalid
   */
  private static String validateAndSetEmail(String email) {
    Objects.requireNonNull(email, "Email must not be null.");
    if (email.isBlank()) {
      throw new IllegalArgumentException("Email must not be empty or contain only whitespace.");
    }
    validateEmail(email);
    return email;
  }

  /**
   * Retrieves the email address of the mail user.
   *
   * @return the email address
   */
  public String getEmail() {
    return email;
  }

  /**
   * Retrieves the name of the mail user.
   *
   * @return the name, or null if not set
   */
  public String getName() {
    return name;
  }

  /**
   * Serializes the mail user to a JSON string using JSON-B.
   *
   * @return a pretty-printed JSON representation of the mail user
   */
  @Override
  public String toString() {
    return JSONB.toJson(this);
  }
}
