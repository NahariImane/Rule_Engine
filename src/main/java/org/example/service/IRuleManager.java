package org.example.service;

import org.example.exception.RuleLoadingException;
import org.example.model.DataObject;
import org.example.model.WorkflowValidationResult;

import java.io.IOException;

public interface IRuleManager {

    void configure(String ruleFilePath) throws IOException, RuleLoadingException;

    WorkflowValidationResult validate(DataObject dataToValidate) throws Exception;

}
