package org.example.model;

import java.io.IOException;
import java.util.Map;

public interface IRuleManager {

    Map<String, ValidationResult> validate(DataObject fieldsToValidate) throws Exception;

    void configure(String ruleFilePath) throws IOException;

}
