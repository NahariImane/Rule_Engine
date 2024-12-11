package org.example.model;

import java.util.Map;

public class WorkflowValidationResult {
    private String workflowName;
    private boolean isValid;
    private Map<String,FieldValidationResult> fieldsValidationResult;
    private Map<String,FieldValidationResult> fieldsInvalidResult;


    public void setWorkflowName(String workflowName){
        this.workflowName = workflowName;
    }

    public void setFieldsResult(Map<String, FieldValidationResult> fieldsValidationResult) {
        this.fieldsValidationResult = fieldsValidationResult;
        boolean isValid = true;
        for (String field : fieldsValidationResult.keySet()) {
            isValid = isValid && fieldsValidationResult.get(field).isValid();
        }
        this.isValid = isValid;
    }

    public void setFieldsInvalidResult(Map<String,FieldValidationResult> fieldsInvalidResult){
        this.fieldsInvalidResult = fieldsInvalidResult;
    }

    public String getWorkflowName(){
        return this.workflowName;
    }

    public Map<String,FieldValidationResult> getFieldsResult(){
        return this.fieldsValidationResult;
    }

    public boolean isValid(){
        return this.isValid;
    }

    public Map<String,FieldValidationResult> getFieldsInvalidResult(){
        return this.fieldsInvalidResult;
    }
}
