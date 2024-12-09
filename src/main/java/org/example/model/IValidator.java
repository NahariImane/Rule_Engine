package org.example.model;

import org.example.exception.RuleLoadingException;

import java.io.IOException;
import java.util.Map;

public interface IValidator {

    void start(ValidatorParam param) throws IOException, RuleLoadingException;

    Map<String, WorkflowValidationResult> validate(DataObject dataToValidate) throws Exception;

}
