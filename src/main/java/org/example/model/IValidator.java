package org.example.model;

import java.util.Map;

public interface IValidator {

    void start(ValidatorParam param);

    Map<String, ValidationResult> validate(Map<String, String> fieldsToValidate) throws Exception;
}
