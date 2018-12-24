package com.connection.customexception;

public class NameValidatorException extends RuntimeException {

    private final String id;

    private final String shortText;

    private String longText;

    public NameValidatorException(String id, String shortText) {
        this.id = id;
        this.shortText = shortText;
    }

    public NameValidatorException(String id, String shortText, String longText) {
        this.id = id;
        this.shortText = shortText;
        this.longText = longText;
    }

    public String getId() {
        return id;
    }

    public String getShortText() {
        return shortText;
    }

    public String getLongText() {
        return longText;
    }
}
