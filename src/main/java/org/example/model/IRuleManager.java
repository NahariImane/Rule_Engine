package org.example.model;

import java.util.Map;

public interface IRuleManager {

    Map<String, ValidationResult> validate(Map<String, String> fieldsToValidate) throws Exception;

    void configure(String ruleFilePath);

}
