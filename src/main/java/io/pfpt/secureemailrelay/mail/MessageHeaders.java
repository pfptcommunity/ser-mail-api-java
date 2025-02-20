package io.pfpt.secureemailrelay.mail;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbProperty;
import java.util.Objects;

public final class MessageHeaders {

  private static final Jsonb JSONB = JsonbBuilder.create(new JsonbConfig().withFormatting(true));

  @JsonbProperty("from")
  private MailUser from;

  public MessageHeaders(MailUser from) {
    this.from = Objects.requireNonNull(from, "Header from address must not be null.");
  }

  public MailUser getFrom() {
    return from;
  }

  public void setFrom(MailUser from) {
    this.from = Objects.requireNonNull(from, "Header from address must not be null.");
  }

  @Override
  public String toString() {
    return JSONB.toJson(this);
  }
}
