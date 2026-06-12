package com.comptafx.metier;


public class ComptaException extends Exception {
    private String errorCode;
    
    public ComptaException(String message) {
        super(message);
    }
    
    public ComptaException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ComptaException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ComptaException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}

