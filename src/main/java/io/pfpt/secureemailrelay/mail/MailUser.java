package io.pfpt.secureemailrelay.mail;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbProperty;
import java.util.Objects;
import java.util.regex.Pattern;

public final class MailUser {

  private static final Pattern EMAIL_PATTERN =
      Pattern.compile(
          "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

  private static final Jsonb JSONB = JsonbBuilder.create(new JsonbConfig().withFormatting(true));

  @JsonbProperty("email")
  private String email;

  @JsonbProperty("name")
  private String name;

  public MailUser(String email) {
    this(email, null);
  }

  public MailUser(String email, String name) {
    setEmail(email);
    setName(name);
  }

  private static void validateEmail(String email) {
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new IllegalArgumentException("Invalid email format: '" + email + "'.");
    }
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    Objects.requireNonNull(email, "Email must not be null.");
    if (email.isBlank()) {
      throw new IllegalArgumentException("Email must not be empty or contain only whitespace.");
    }
    validateEmail(email);
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return JSONB.toJson(this);
  }
}
