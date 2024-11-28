package org.example;

import org.example.model.*;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {

        System.out.println("Hello world!");

        IValidator myValidator = new ValidatorImpl();
        ValidatorParam param = new ValidatorParam("src/main/Configuration/RulesTest.xlsx", ValidatorTypeEnum.DATA);
        myValidator.start(param);

        System.out.println("------------DATA------------------");
        Map<String, String> fieldsToValidate = new HashMap<>();
        fieldsToValidate.put("PRENOM", "Aline");
        fieldsToValidate.put("NOM", "Zhang");
        fieldsToValidate.put("DATE_NAISSANCE", "01/01/2009");
        fieldsToValidate.put("STATUT", "minor");
        fieldsToValidate.put("TYPE_TITRE", "PSP");
        fieldsToValidate.put("SEXE", "femme");
        fieldsToValidate.put("PRENOM_USUEL", "Aline");
        fieldsToValidate.put("TAILLE", "1.55");
        fieldsToValidate.put("PAYS_NAISSANCE", "france");
        fieldsToValidate.put("DEPARTEMENT_NAISSANCE", "011");

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
