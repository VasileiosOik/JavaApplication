package com.connection.customexception;

import static com.connection.customexception.ExceptionMessageBuilder.exceptionMessageBuilder;

public class AlreadyCreatedException extends RuntimeException {

    public AlreadyCreatedException(String contextEntityName) {
        super(exceptionMessageBuilder().withDescription(String.format(" %s not created because it exists", contextEntityName)).build());
    }
}
