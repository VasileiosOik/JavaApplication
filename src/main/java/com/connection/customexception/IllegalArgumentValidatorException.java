package com.connection.customexception;

public class IllegalArgumentValidatorException extends RuntimeException {

    private final String id;

    private final String shortText;

    private final String longText;


    public IllegalArgumentValidatorException(String id, String shortText, String longText) {
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
