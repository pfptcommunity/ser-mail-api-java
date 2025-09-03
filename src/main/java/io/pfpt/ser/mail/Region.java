package io.pfpt.ser.mail;

public enum Region {
    US("mail-us.ser.proofpoint.com"),
    CA("mail-ca.ser.proofpoint.com"),
    EU("mail-eu.ser.proofpoint.com"),
    AU("mail-aus.ser.proofpoint.com");

    private final String value;

    Region(String value) {
        this.value = value;
    }

    String getString() {
        return value;
    }
}