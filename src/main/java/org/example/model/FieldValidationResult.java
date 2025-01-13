package org.example.model;

public class FieldValidationResult {

    private final boolean valid;
    private final String message;


    public FieldValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }


    public String getMessage() {
        return message;
    }

}
