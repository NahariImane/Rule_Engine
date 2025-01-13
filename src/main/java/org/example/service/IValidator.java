package org.example.service;

import org.example.exception.RuleLoadingException;
import org.example.model.DataObject;
import org.example.model.ValidatorParam;
import org.example.model.WorkflowValidationResult;

import java.io.IOException;

public interface IValidator {

    void start(ValidatorParam param) throws IOException, RuleLoadingException;

    WorkflowValidationResult validate(DataObject dataToValidate) throws Exception;
}
