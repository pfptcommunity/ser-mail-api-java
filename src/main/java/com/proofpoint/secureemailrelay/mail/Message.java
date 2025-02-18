package com.proofpoint.secureemailrelay.mail;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Message {

    @JsonbProperty("attachments")
    private final List<Attachment> attachments = new ArrayList<>();

    @JsonbProperty("content")
    private final List<Content> content = new ArrayList<>();

    @JsonbProperty("from")
    private final MailUser from;

    @JsonbProperty("headers")
    private MessageHeaders headers;

    @JsonbProperty("subject")
    private final String subject;

    @JsonbProperty("tos")
    private final List<MailUser> tos = new ArrayList<>();

    @JsonbProperty("cc")
    private final List<MailUser> cc = new ArrayList<>();

    @JsonbProperty("bcc")
    private final List<MailUser> bcc = new ArrayList<>();

    @JsonbProperty("replyTos")
    private final List<MailUser> replyTos = new ArrayList<>();

    private static final Jsonb JSONB = JsonbBuilder.create(new JsonbConfig().withFormatting(true));

    public Message(String subject, MailUser from, MailUser headerFrom) {
        this(subject, from);
        setHeaderFrom(headerFrom);
    }

    public Message(String subject, MailUser from) {
        this.subject = Objects.requireNonNull(subject, "Subject must not be null.");
        this.from = Objects.requireNonNull(from, "Sender must not be null.");
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public List<Content> getContent() {
        return content;
    }

    public MailUser getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public List<MailUser> getTos() {
        return tos;
    }

    public List<MailUser> getCc() {
        return cc;
    }

    public List<MailUser> getBcc() {
        return bcc;
    }

    public List<MailUser> getReplyTos() {
        return replyTos;
    }

    @JsonbTransient
    public MailUser getHeaderFrom() {
        return headers != null ? headers.getFrom() : null;
    }

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
    public String toString() {
        return JSONB.toJson(this);
    }
}
