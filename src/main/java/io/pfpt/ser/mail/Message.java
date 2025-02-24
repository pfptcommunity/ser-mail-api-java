package io.pfpt.ser.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;

/**
 * Represents an email message with sender, recipients, content, and attachments.
 * Supports construction via traditional constructors for backward compatibility
 * and a fluent builder pattern for flexible instantiation with build-time validation.
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
  private MessageHeaders headers;

  /**
   * Constructs a Message with a subject and sender, initializing empty lists for other fields.
   * Used by existing code for backward compatibility.
   *
   * @param subject the subject of the email
   * @param from the sender of the email
   * @throws NullPointerException if subject or from is null
   */
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

  /**
   * Constructs a Message with a subject, sender, and header sender.
   * Delegates to the simpler constructor and sets the headerFrom field.
   * Preserved for backward compatibility with existing code.
   *
   * @param subject the subject of the email
   * @param from the sender of the email
   * @param headerFrom the sender to appear in the email headers
   * @throws NullPointerException if subject or from is null
   */
  public Message(String subject, MailUser from, MailUser headerFrom) {
    this(subject, from);
    setHeaderFrom(headerFrom);
  }

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
    setHeaderFrom(headerFrom);
    this.attachments = new ArrayList<>(attachments); // Defensive copy
    this.content = new ArrayList<>(content);
    this.tos = new ArrayList<>(tos);
    this.cc = new ArrayList<>(cc);
    this.bcc = new ArrayList<>(bcc);
    this.replyTos = new ArrayList<>(replyTos);
  }

  /**
   * Retrieves the list of attachments.
   *
   * @return the list of attachments
   */
  public List<Attachment> getAttachments() { return attachments; }

  /**
   * Retrieves the list of content items.
   *
   * @return the list of content items
   */
  public List<Content> getContent() { return content; }

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
  public List<MailUser> getTos() { return tos; }

  /**
   * Retrieves the list of CC recipients.
   *
   * @return the list of CC recipients
   */
  public List<MailUser> getCc() { return cc; }

  /**
   * Retrieves the list of BCC recipients.
   *
   * @return the list of BCC recipients
   */
  public List<MailUser> getBcc() { return bcc; }

  /**
   * Retrieves the list of Reply-To recipients.
   *
   * @return the list of Reply-To recipients
   */
  public List<MailUser> getReplyTos() { return replyTos; }

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
   * Sets the header "From" value, creating or updating the headers field as needed.
   *
   * @param headerFrom the sender to appear in the email headers, or null to clear headers
   */
  public void setHeaderFrom(MailUser headerFrom) {
    if (headerFrom == null) {
      this.headers = null;
    } else if (this.headers == null) {
      this.headers = new MessageHeaders(headerFrom);
    } else {
      this.headers.setFrom(headerFrom);
    }
  }

  /**
   * Adds an attachment to the email.
   *
   * @param attachment the attachment to add
   * @throws NullPointerException if attachment is null
   */
  public void addAttachment(Attachment attachment) {
    attachments.add(Objects.requireNonNull(attachment, "Attachment must not be null."));
  }

  /**
   * Adds a content item to the email.
   *
   * @param contentItem the content item to add
   * @throws NullPointerException if contentItem is null
   */
  public void addContent(Content contentItem) {
    content.add(Objects.requireNonNull(contentItem, "Content must not be null."));
  }

  /**
   * Adds a primary recipient (To field) to the email.
   *
   * @param to the recipient to add
   * @throws NullPointerException if to is null
   */
  public void addTo(MailUser to) {
    tos.add(Objects.requireNonNull(to, "Recipient must not be null."));
  }

  /**
   * Adds a CC recipient to the email.
   *
   * @param ccUser the CC recipient to add
   * @throws NullPointerException if ccUser is null
   */
  public void addCc(MailUser ccUser) {
    cc.add(Objects.requireNonNull(ccUser, "CC recipient must not be null."));
  }

  /**
   * Adds a BCC recipient to the email.
   *
   * @param bccUser the BCC recipient to add
   * @throws NullPointerException if bccUser is null
   */
  public void addBcc(MailUser bccUser) {
    bcc.add(Objects.requireNonNull(bccUser, "BCC recipient must not be null."));
  }

  /**
   * Adds a Reply-To recipient to the email.
   *
   * @param replyToUser the Reply-To recipient to add
   * @throws NullPointerException if replyToUser is null
   */
  public void addReplyTo(MailUser replyToUser) {
    replyTos.add(Objects.requireNonNull(replyToUser, "Reply-To recipient must not be null."));
  }

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
    // Temporary fields for building the Message
    private String subject;
    private MailUser from;
    private MailUser headerFrom;
    private List<Attachment> attachments = new ArrayList<>();
    private List<Content> content = new ArrayList<>();
    private List<MailUser> tos = new ArrayList<>();
    private List<MailUser> cc = new ArrayList<>();
    private List<MailUser> bcc = new ArrayList<>();
    private List<MailUser> replyTos = new ArrayList<>();

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

  /**
   * Provides a factory method to create a new Builder instance.
   *
   * @return a new Builder for constructing a Message
   */
  public static Builder builder() {
    return new Builder();
  }
}