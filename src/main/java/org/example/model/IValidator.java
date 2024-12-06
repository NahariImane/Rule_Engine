package org.example.model;

import java.io.IOException;
import java.util.Map;

public interface IValidator {

    void start(ValidatorParam param) throws IOException;

    Map<String, ValidationResult> validate(DataObject dataToValidate) throws Exception;

    DataObject convertJsonToDataObject(String jsonDataFile);
}
