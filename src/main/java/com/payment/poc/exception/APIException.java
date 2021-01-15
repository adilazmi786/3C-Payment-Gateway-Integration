package com.payment.poc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class APIException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public APIException(String message) {
        super(message);
    }

}
