package org.example.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IRuleManager {

    Map<String, WorkflowValidationResult> validate(DataObject fieldsToValidate) throws Exception;

    void configure(String ruleFilePath) throws IOException;

}
