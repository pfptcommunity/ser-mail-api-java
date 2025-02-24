package io.pfpt.ser.mail;

/**
 * Defines a contract for mapping file names to MIME types and validating MIME types.
 * Implementations of this interface provide mechanisms to determine the MIME type of a file based
 * on its name and check the validity of a given MIME type.
 */
public interface IMimeMapper {

  /**
   * Retrieves the MIME type associated with a given file name. Typically, this is determined based
   * on the file's extension.
   *
   * @param fileName the name of the file to analyze
   * @return the corresponding MIME type as a string
   */
  String getMimeType(String fileName);

  /**
   * Checks if a given MIME type is valid according to the mapper's criteria.
   *
   * @param mimeType the MIME type to validate
   * @return true if the MIME type is valid, false otherwise
   */
  boolean isValidMimeType(String mimeType);
}
