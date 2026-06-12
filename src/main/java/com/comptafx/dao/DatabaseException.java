package com.comptafx.dao;

import com.comptafx.metier.ComptaException;

/**
 * Exception thrown when database operations fail
 * Defined in DatabaseConfig.java context
 */
public class DatabaseException extends ComptaException {
    public DatabaseException(String message) {
        super(message, "DB_ERROR");
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, "DB_ERROR", cause);
    }
}


