package org.example.model;

import java.util.Map;

public class WorkflowValidationResult {
    private boolean isValid;
    private Map<String,FieldValidationResult> fieldsValidationResult;



    public void setFieldsResult(Map<String, FieldValidationResult> fieldsValidationResult) {
        this.fieldsValidationResult = fieldsValidationResult;
        boolean isValid = true;
        for (String field : fieldsValidationResult.keySet()) {
            isValid = isValid && fieldsValidationResult.get(field).isValid();
        }
        this.isValid = isValid;
    }

    public Map<String,FieldValidationResult> getFieldsResult(){
        return this.fieldsValidationResult;
    }

    public boolean isValid(){
        return this.isValid;
    }
}
