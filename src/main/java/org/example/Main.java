package org.example;

import org.example.model.*;
import org.example.service.RuleManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {

        System.out.println("Hello world!");

        IValidator myValidator = new ValidatorImpl();
        ValidatorParam param = new ValidatorParam("filePath/file.csv", ValidatorTypeEnum.DATA);
        myValidator.start(param);

        System.out.println("------------DATA------------------");
        Map<String, String> fieldsToValidate = new HashMap<>();
        fieldsToValidate.put("NOM", "");
        fieldsToValidate.put("DATE_NAISSANCE", "2009-01-01");
        fieldsToValidate.put("MINEUR_MAJEUR", "MINEUR");
        fieldsToValidate.put("TYPE_TITRE", "PSP");

        for(Map.Entry<String, String> entry : fieldsToValidate.entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();

            System.out.println(field + " : " + value);
        }

        System.out.println("------------VALIDATION------------------");
        Map<String, ValidationResult> validationResponse = myValidator.validate(fieldsToValidate);

        System.out.println("------------RESULTAT------------------");
        for(Map.Entry<String, ValidationResult> entry : validationResponse.entrySet()) {
            String field = entry.getKey();
            ValidationResult result = entry.getValue();

            System.out.println(field + " : " + result.isValid());
        }
    }
}
