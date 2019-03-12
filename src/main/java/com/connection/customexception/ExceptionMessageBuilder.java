package com.connection.customexception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class ExceptionMessageBuilder {

    private String message;
    private String reason;
    private String description;

    static ExceptionMessageBuilder exceptionMessageBuilder() {
        return new ExceptionMessageBuilder();
    }

    public ExceptionMessageBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    ExceptionMessageBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    ExceptionMessageBuilder withReason(String reason) {
        this.reason = reason;
        return this;
    }

    String build() {
        if (!StringUtils.isEmpty(message)) {
            return message;
        } else {
            Assert.state(description != null, "Description is a mandatory field");
            return String.format("%s", description);
        }
    }
}
