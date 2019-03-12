package com.connection.customexception;

import static com.connection.customexception.ExceptionMessageBuilder.exceptionMessageBuilder;

public class NotFoundException extends RuntimeException {

    public static NotFoundException withReason(String reason) {
        return new NotFoundException(reason);
    }

    public NotFoundException(String reason) {
        super(exceptionMessageBuilder().withDescription("Entity Not Found").withReason(reason).build());
    }
}
