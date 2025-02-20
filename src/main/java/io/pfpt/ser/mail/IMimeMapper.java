package io.pfpt.ser.mail;

public interface IMimeMapper {
  String getMimeType(String fileName);

  boolean isValidMimeType(String mimeType);
}
