package io.pfpt.secureemailrelay.mail;

public interface IMimeMapper {
  String getMimeType(String fileName);

  boolean isValidMimeType(String mimeType);
}
