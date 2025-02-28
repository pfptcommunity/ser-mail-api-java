package io.pfpt.ser.mail;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents an email message with sender, recipients, content, and attachments.
 * This class uses a fluent builder pattern for instantiation with build-time validation.
 * Instances of this class are immutable once constructed.
 */
public final class Message {
  // JSON-B instance configured for pretty-printed serialization of the message
  private static final Jsonb JSONB = JsonbBuilder.create(new JsonbConfig().withFormatting(true));

  // List of file attachments for the email
  @JsonbProperty("attachments")
  private final List<Attachment> attachments;

  // List of content bodies (e.g., text or HTML) for the email
  @JsonbProperty("content")
  private final List<Content> content;

  // The sender of the email
  @JsonbProperty("from")
  private final MailUser from;

  // The subject line of the email
  @JsonbProperty("subject")
  private final String subject;

  // List of primary recipients (To field)
  @JsonbProperty("tos")
  private final List<MailUser> tos;

  // List of carbon copy (CC) recipients
  @JsonbProperty("cc")
  private final List<MailUser> cc;

  // List of blind carbon copy (BCC) recipients
  @JsonbProperty("bcc")
  private final List<MailUser> bcc;

  // List of Reply-To recipients
  @JsonbProperty("replyTos")
  private final List<MailUser> replyTos;

  // Email headers, currently supporting only the "From" header
  @JsonbProperty("headers")
  private final MessageHeaders headers;

  /**
   * Package-private constructor for builder use, initializing all fields.
   * Ensures immutable field initialization with defensive copies for lists.
   *
   * @param subject the subject of the email
   * @param from the sender of the email
   * @param headerFrom the sender to appear in the email headers (optional)
   * @param attachments list of attachments
   * @param content list of content items
   * @param tos list of primary recipients
   * @param cc list of CC recipients
   * @param bcc list of BCC recipients
   * @param replyTos list of Reply-To recipients
   * @throws NullPointerException if subject or from is null
   */
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
    this.headers = headerFrom != null ? new MessageHeaders(headerFrom) : null;
    this.attachments = new ArrayList<>(attachments); // Defensive copy
    this.content = new ArrayList<>(content);
    this.tos = new ArrayList<>(tos);
    this.cc = new ArrayList<>(cc);
    this.bcc = new ArrayList<>(bcc);
    this.replyTos = new ArrayList<>(replyTos);
  }

  /**
   * Provides a factory method to create a new Builder instance.
   *
   * @return a new Builder for constructing a Message
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Retrieves the list of attachments.
   *
   * @return the list of attachments
   */
  public List<Attachment> getAttachments() { return Collections.unmodifiableList(attachments); }

  /**
   * Retrieves the list of content items.
   *
   * @return the list of content items
   */
  public List<Content> getContent() { return Collections.unmodifiableList(content); }

  /**
   * Retrieves the sender of the email.
   *
   * @return the sender as a MailUser object
   */
  public MailUser getFrom() { return from; }

  /**
   * Retrieves the subject of the email.
   *
   * @return the subject string
   */
  public String getSubject() { return subject; }

  /**
   * Retrieves the list of primary recipients (To field).
   *
   * @return the list of To recipients
   */
  public List<MailUser> getTos() { return Collections.unmodifiableList(tos); }

  /**
   * Retrieves the list of CC recipients.
   *
   * @return the list of CC recipients
   */
  public List<MailUser> getCc() { return Collections.unmodifiableList(cc); }

  /**
   * Retrieves the list of BCC recipients.
   *
   * @return the list of BCC recipients
   */
  public List<MailUser> getBcc() { return Collections.unmodifiableList(bcc); }

  /**
   * Retrieves the list of Reply-To recipients.
   *
   * @return the list of Reply-To recipients
   */
  public List<MailUser> getReplyTos() { return Collections.unmodifiableList(replyTos); }

  /**
   * Retrieves the email headers.
   *
   * @return the MessageHeaders object, or null if not set
   */
  public MessageHeaders getHeaders() { return headers; }

  /**
   * Retrieves the header "From" value, if set.
   * Excluded from JSON serialization.
   *
   * @return the header From as a MailUser, or null if headers is not set
   */
  @JsonbTransient
  public MailUser getHeaderFrom() { return headers != null ? headers.getFrom() : null; }

  /**
   * Serializes the message to a JSON string using JSON-B.
   *
   * @return a pretty-printed JSON representation of the message
   */
  @Override
  public String toString() { return JSONB.toJson(this); }

  /**
   * Builder class for constructing Message instances fluently.
   * Enforces minimum requirements (from, tos, subject, content) at build time.
   */
  public static class Builder {
    private final List<Attachment> attachments = new ArrayList<>();
    private final List<Content> content = new ArrayList<>();
    private final List<MailUser> tos = new ArrayList<>();
    private final List<MailUser> cc = new ArrayList<>();
    private final List<MailUser> bcc = new ArrayList<>();
    private final List<MailUser> replyTos = new ArrayList<>();
    // Temporary fields for building the Message
    private String subject;
    private MailUser from;
    private MailUser headerFrom;

    // Package-private constructor to restrict instantiation to within the package
    Builder() { }

    /**
     * Sets the subject of the email.
     *
     * @param subject the subject to set
     * @return this Builder for chaining
     */
    public Builder subject(String subject) {
      this.subject = subject;
      return this;
    }

    /**
     * Sets the sender of the email.
     *
     * @param from the sender to set
     * @return this Builder for chaining
     */
    public Builder from(MailUser from) {
      this.from = from;
      return this;
    }

    public Builder from(String email, String name) { this.from = new MailUser(email, name); return this; }
    public Builder from(String email) { return from(email, null); }

    /**
     * Sets the header "From" value.
     *
     * @param headerFrom the sender to appear in the headers, or null
     * @return this Builder for chaining
     */
    public Builder headerFrom(MailUser headerFrom) {
      this.headerFrom = headerFrom;
      return this;
    }
    public Builder headerFrom(String email, String name) { this.headerFrom = new MailUser(email, name); return this; }
    public Builder headerFrom(String email) { return headerFrom(email, null); }

    /**
     * Adds an attachment to the email.
     *
     * @param attachment the attachment to add
     * @return this Builder for chaining
     * @throws NullPointerException if attachment is null
     */
    public Builder addAttachment(Attachment attachment) {
      this.attachments.add(Objects.requireNonNull(attachment, "Attachment must not be null."));
      return this;
    }

    /**
     * Adds a content item to the email.
     *
     * @param content the content item to add
     * @return this Builder for chaining
     * @throws NullPointerException if content is null
     */
    public Builder addContent(Content content) {
      this.content.add(Objects.requireNonNull(content, "Content must not be null."));
      return this;
    }
    public Builder addContent(String body, Content.ContentType type) {
      this.content.add(new Content(body, type));
      return this;
    }
    /**
     * Adds a primary recipient (To field) to the email.
     *
     * @param to the recipient to add
     * @return this Builder for chaining
     * @throws NullPointerException if to is null
     */
    public Builder addTo(MailUser to) {
      this.tos.add(Objects.requireNonNull(to, "Recipient must not be null."));
      return this;
    }
    public Builder addTo(String email, String name) { this.tos.add(new MailUser(email, name)); return this; }
    public Builder addTo(String email) { return addTo(email, null); }
    /**
     * Adds a CC recipient to the email.
     *
     * @param cc the CC recipient to add
     * @return this Builder for chaining
     * @throws NullPointerException if cc is null
     */
    public Builder addCc(MailUser cc) {
      this.cc.add(Objects.requireNonNull(cc, "CC recipient must not be null."));
      return this;
    }
    public Builder addCc(String email, String name) { this.cc.add(new MailUser(email, name)); return this; }
    public Builder addCc(String email) { return addCc(email, null); }
    /**
     * Adds a BCC recipient to the email.
     *
     * @param bcc the BCC recipient to add
     * @return this Builder for chaining
     * @throws NullPointerException if bcc is null
     */
    public Builder addBcc(MailUser bcc) {
      this.bcc.add(Objects.requireNonNull(bcc, "BCC recipient must not be null."));
      return this;
    }
    public Builder addBcc(String email, String name) { this.bcc.add(new MailUser(email, name)); return this; }
    public Builder addBcc(String email) { return addBcc(email, null); }

    /**
     * Adds a Reply-To recipient to the email.
     *
     * @param replyTo the Reply-To recipient to add
     * @return this Builder for chaining
     * @throws NullPointerException if replyTo is null
     */
    public Builder addReplyTo(MailUser replyTo) {
      this.replyTos.add(Objects.requireNonNull(replyTo, "Reply-To recipient must not be null."));
      return this;
    }
    public Builder addReplyTo(String email, String name) { this.replyTos.add(new MailUser(email, name)); return this; }
    public Builder addReplyTo(String email) { return addReplyTo(email, null); }
    /**
     * Builds a Message instance with the configured fields.
     * Enforces minimum requirements for a valid email message.
     *
     * @return a new Message instance
     * @throws IllegalStateException if from, tos, subject, or content is not set appropriately
     */
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
}