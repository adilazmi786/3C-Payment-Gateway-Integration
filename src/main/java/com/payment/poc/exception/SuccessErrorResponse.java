package com.payment.poc.exception;

public class SuccessErrorResponse {

    private String message;
    private String description;
    private String cause;
    
    public SuccessErrorResponse(String message, String description) {
        super();
        this.message = message;
        this.description = description;
    }
    
    
    public SuccessErrorResponse(String message, String description, String cause) {
        super();
        this.message = message;
        this.description = description;
        this.cause = cause;
    }


    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getCause() {
        return cause;
    }
    public void setCause(String cause) {
        this.cause = cause;
    }
   
    
}
