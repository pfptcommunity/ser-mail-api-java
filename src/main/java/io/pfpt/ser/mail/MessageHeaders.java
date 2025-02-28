package io.pfpt.ser.mail;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbProperty;
import java.util.Objects;

/**
 * Represents the headers of an email message, specifically the sender information. This class
 * encapsulates the "From" field of an email header and provides JSON serialization. Instances of
 * this class are immutable once constructed.
 */
public final class MessageHeaders {

  // JSON-B instance configured for pretty-printed serialization
  private static final Jsonb JSONB = JsonbBuilder.create(new JsonbConfig().withFormatting(true));

  // The sender of the email, represented as a MailUser object
  @JsonbProperty("from")
  private final MailUser from;

  /**
   * Constructs a MessageHeaders instance with the specified sender.
   *
   * @param from the MailUser representing the sender of the email
   * @throws NullPointerException if the from parameter is null
   */
  public MessageHeaders(MailUser from) {
    this.from = Objects.requireNonNull(from, "Header from address must not be null.");
  }

  /**
   * Retrieves the sender of the email.
   *
   * @return the MailUser object representing the sender
   */
  public MailUser getFrom() {
    return from;
  }

  /**
   * Serializes the message headers to a JSON string using JSON-B.
   *
   * @return a pretty-printed JSON representation of the headers
   */
  @Override
  public String toString() {
    return JSONB.toJson(this);
  }
}
