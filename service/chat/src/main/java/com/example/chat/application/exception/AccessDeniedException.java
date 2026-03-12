package com.example.chat.application.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
    public AccessDeniedException(String message, Throwable cause){
        super(message, cause);
    }
}
