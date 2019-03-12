package com.connection.customexception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().enable(SerializationFeature.WRAP_ROOT_VALUE);

    private static final Map<Class<? extends RuntimeException>, HttpStatus> EXCEPTION_CLASS_TO_HTTP_STATUS_CODE;

    static {
        Map<Class<? extends RuntimeException>, HttpStatus> map = new HashMap<>();
        map.put(NotFoundException.class, HttpStatus.NOT_FOUND);
        map.put(AlreadyCreatedException.class, HttpStatus.CONFLICT);
        EXCEPTION_CLASS_TO_HTTP_STATUS_CODE = Collections.unmodifiableMap(map);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleException(RuntimeException e) {
        LOGGER.debug("Caught " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        return handleCaughtException(e);
    }

    private ResponseEntity<Object> handleCaughtException(RuntimeException e) {
        return createResponseEntity(getSerializedWrappedMessage(e, e.getMessage()), EXCEPTION_CLASS_TO_HTTP_STATUS_CODE.get(e.getClass()));
    }

    private ResponseEntity<Object> createResponseEntity(String serializedWrappedMessage, HttpStatus httpStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(serializedWrappedMessage, headers, httpStatus);
    }

    private String getSerializedWrappedMessage(Exception e, String message) {
        try {
            return OBJECT_MAPPER.writeValueAsString(message);
        } catch (JsonProcessingException jpe) {
            RuntimeException exception = new RuntimeException(e);
            return getSerializedWrappedMessage(exception, jpe.getMessage());
        }
    }


}
