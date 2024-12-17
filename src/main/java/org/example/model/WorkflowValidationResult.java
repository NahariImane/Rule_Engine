package org.example.model;

import java.util.ArrayList;
import java.util.Map;

public class WorkflowValidationResult {
    private ArrayList<String> workflowNames;
    private boolean isValid;
    private Map<String,FieldValidationResult> fieldsValidationResult;
    private Map<String,FieldValidationResult> fieldsInvalidResult;


    public void addWorkflowName(String workflowName){
        if(workflowNames == null){
            workflowNames = new ArrayList<>();
        }
        workflowNames.add(workflowName);
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

    public ArrayList<String> getWorkflowNames(){
        return this.workflowNames;
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
