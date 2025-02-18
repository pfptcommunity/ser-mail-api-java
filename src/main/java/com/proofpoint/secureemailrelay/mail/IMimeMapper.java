package com.proofpoint.secureemailrelay.mail;

public interface IMimeMapper {
    String getMimeType(String fileName);

    boolean isValidMimeType(String mimeType);
}
