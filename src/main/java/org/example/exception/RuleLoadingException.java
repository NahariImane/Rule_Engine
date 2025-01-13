package org.example.exception;

public class RuleLoadingException extends Exception {
    public RuleLoadingException(String message) {
        super(message);
    }

    public RuleLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
