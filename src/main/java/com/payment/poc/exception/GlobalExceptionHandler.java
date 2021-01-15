package com.payment.poc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(APIException.class)
    public ResponseEntity<?> handleAPIException(WebRequest webRequest, APIException exception) {

        SuccessErrorResponse err = new SuccessErrorResponse(exception.getMessage(), webRequest.getDescription(false));

        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(WebRequest webRequest, Exception exception) {

        SuccessErrorResponse err = new SuccessErrorResponse(exception.getMessage(), webRequest.getDescription(true),
                exception.getCause().toString());

        return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
