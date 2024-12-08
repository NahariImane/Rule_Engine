package org.example.model;

import java.io.IOException;
import java.util.Map;

public interface IValidator {

    void start(ValidatorParam param) throws IOException;

    Map<String, WorkflowValidationResult> validate(DataObject dataToValidate) throws Exception;

}
