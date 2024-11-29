package org.example.model;

public class ValidationOutput {
    private String fieldName;
    private boolean isValid;
    private String errorMessage;
    private String ruleViolated;

    public ValidationOutput(String fieldName, boolean isValid, String errorMessage, String ruleViolated) {
        this.fieldName = fieldName;
        this.isValid = isValid;
        this.errorMessage = errorMessage;
        this.ruleViolated = ruleViolated;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getRuleViolated() {
        return ruleViolated;
    }

    @Override
    public String toString() {
        if (isValid) {
            return fieldName + " : Valide";
        } else {
            return fieldName + " : Invalide (" + errorMessage + ") - Règle non respectée : " + ruleViolated;
        }
    }
}
