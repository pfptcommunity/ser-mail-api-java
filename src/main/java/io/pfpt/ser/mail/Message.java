package io.pfpt.ser.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;

public final class Message {
  private static final Jsonb JSONB = JsonbBuilder.create(new JsonbConfig().withFormatting(true));

  @JsonbProperty("attachments")
  private final List<Attachment> attachments;

  @JsonbProperty("content")
  private final List<Content> content;

  @JsonbProperty("from")
  private final MailUser from;

  @JsonbProperty("subject")
  private final String subject;

  @JsonbProperty("tos")
  private final List<MailUser> tos;

  @JsonbProperty("cc")
  private final List<MailUser> cc;

  @JsonbProperty("bcc")
  private final List<MailUser> bcc;

  @JsonbProperty("replyTos")
  private final List<MailUser> replyTos;

  @JsonbProperty("headers")
  private MessageHeaders headers;

  // Original constructor with subject and from
  public Message(String subject, MailUser from) {
    this.subject = Objects.requireNonNull(subject, "Subject must not be null.");
    this.from = Objects.requireNonNull(from, "Sender must not be null.");
    this.attachments = new ArrayList<>();
    this.content = new ArrayList<>();
    this.tos = new ArrayList<>();
    this.cc = new ArrayList<>();
    this.bcc = new ArrayList<>();
    this.replyTos = new ArrayList<>();
    this.headers = null;
  }

  // Original constructor with subject, from, and headerFrom
  public Message(String subject, MailUser from, MailUser headerFrom) {
    this(subject, from);
    setHeaderFrom(headerFrom);
  }

  // New package-private constructor for builder
  Message(
          String subject,
          MailUser from,
          MailUser headerFrom,
          List<Attachment> attachments,
          List<Content> content,
          List<MailUser> tos,
          List<MailUser> cc,
          List<MailUser> bcc,
          List<MailUser> replyTos) {
    this.subject = Objects.requireNonNull(subject, "Subject must not be null.");
    this.from = Objects.requireNonNull(from, "Sender must not be null.");
    setHeaderFrom(headerFrom);
    this.attachments = new ArrayList<>(attachments); // Defensive copy
    this.content = new ArrayList<>(content);
    this.tos = new ArrayList<>(tos);
    this.cc = new ArrayList<>(cc);
    this.bcc = new ArrayList<>(bcc);
    this.replyTos = new ArrayList<>(replyTos);
  }

  // Existing getters and setters unchanged
  public List<Attachment> getAttachments() { return attachments; }
  public List<Content> getContent() { return content; }
  public MailUser getFrom() { return from; }
  public String getSubject() { return subject; }
  public List<MailUser> getTos() { return tos; }
  public List<MailUser> getCc() { return cc; }
  public List<MailUser> getBcc() { return bcc; }
  public List<MailUser> getReplyTos() { return replyTos; }
  public MessageHeaders getHeaders() { return headers; }
  @JsonbTransient
  public MailUser getHeaderFrom() { return headers != null ? headers.getFrom() : null; }
  public void setHeaderFrom(MailUser headerFrom) {
    if (headerFrom == null) {
      this.headers = null;
    } else if (this.headers == null) {
      this.headers = new MessageHeaders(headerFrom);
    } else {
      this.headers.setFrom(headerFrom);
    }
  }

  public void addAttachment(Attachment attachment) {
    attachments.add(Objects.requireNonNull(attachment, "Attachment must not be null."));
  }
  public void addContent(Content contentItem) {
    content.add(Objects.requireNonNull(contentItem, "Content must not be null."));
  }
  public void addTo(MailUser to) {
    tos.add(Objects.requireNonNull(to, "Recipient must not be null."));
  }
  public void addCc(MailUser ccUser) {
    cc.add(Objects.requireNonNull(ccUser, "CC recipient must not be null."));
  }
  public void addBcc(MailUser bccUser) {
    bcc.add(Objects.requireNonNull(bccUser, "BCC recipient must not be null."));
  }
  public void addReplyTo(MailUser replyToUser) {
    replyTos.add(Objects.requireNonNull(replyToUser, "Reply-To recipient must not be null."));
  }

  @Override
  public String toString() { return JSONB.toJson(this); }

  // New static builder class
  public static class Builder {
    private String subject;
    private MailUser from;
    private MailUser headerFrom;
    private List<Attachment> attachments = new ArrayList<>();
    private List<Content> content = new ArrayList<>();
    private List<MailUser> tos = new ArrayList<>();
    private List<MailUser> cc = new ArrayList<>();
    private List<MailUser> bcc = new ArrayList<>();
    private List<MailUser> replyTos = new ArrayList<>();

    // Package-private constructor to restrict instantiation to package
    Builder() { }

    public Builder subject(String subject) {
      this.subject = subject;
      return this;
    }

    public Builder from(MailUser from) {
      this.from = from;
      return this;
    }

    public Builder headerFrom(MailUser headerFrom) {
      this.headerFrom = headerFrom;
      return this;
    }

    public Builder addAttachment(Attachment attachment) {
      this.attachments.add(Objects.requireNonNull(attachment, "Attachment must not be null."));
      return this;
    }

    public Builder addContent(Content content) {
      this.content.add(Objects.requireNonNull(content, "Content must not be null."));
      return this;
    }

    public Builder addTo(MailUser to) {
      this.tos.add(Objects.requireNonNull(to, "Recipient must not be null."));
      return this;
    }

    public Builder addCc(MailUser cc) {
      this.cc.add(Objects.requireNonNull(cc, "CC recipient must not be null."));
      return this;
    }

    public Builder addBcc(MailUser bcc) {
      this.bcc.add(Objects.requireNonNull(bcc, "BCC recipient must not be null."));
      return this;
    }

    public Builder addReplyTo(MailUser replyTo) {
      this.replyTos.add(Objects.requireNonNull(replyTo, "Reply-To recipient must not be null."));
      return this;
    }

    public Message build() {
      if (from == null) throw new IllegalStateException("Sender (from) is required.");
      if (tos.isEmpty()) throw new IllegalStateException("At least one recipient (to) is required.");
      if (subject == null) throw new IllegalStateException("Subject is required.");
      if (content.isEmpty()) throw new IllegalStateException("At least one content item is required.");

      return new Message(
              subject,
              from,
              headerFrom,
              attachments,
              content,
              tos,
              cc,
              bcc,
              replyTos
      );
    }
  }

  // Public factory method for builder instantiation
  public static Builder builder() {
    return new Builder();
  }
}