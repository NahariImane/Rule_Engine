package org.example.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.*;
import org.example.service.ValidatorImpl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {



        IValidator myValidator = new ValidatorImpl();
        ValidatorParam param = new ValidatorParam("src/main/Configuration/RulesTest.xlsx", ValidatorTypeEnum.DATA);
        myValidator.start(param);


        DataObject data = myValidator.loadData("src/main/java/org/example/test/dataTest.json");

        // Affichage des données ajoutées dans DataObject
        System.out.println("------------DATA FROM DataObject------------------");
        for (Map.Entry<String, String> entry : data.getFields().entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        // Conversion de DataObject en Map<String, String> pour la validation
//        Map<String, String> fieldsToValidate = new HashMap<>();
//        input.getFields().forEach((key, value) -> fieldsToValidate.put(key, value.toString()));


        System.out.println("------------VALIDATION------------------");
        Map<String, ValidationResult> validationResponse = myValidator.validate(data);


        // Affichage des résultats de validation
        System.out.println("------------RESULTAT------------------");
        for (Map.Entry<String, ValidationResult> entry : validationResponse.entrySet()) {
            String field = entry.getKey();
            ValidationResult result = entry.getValue();
            System.out.println(field + " : " + (result.isValid() ? "Valide" : "Invalide"));
        }


        // Transformation des résultats en objets ValidationOutput
        /*List<ValidationOutput> validationOutputs = new ArrayList<>();
        validationResponse.forEach((field, result) -> {
            String ruleViolated = result.isValid() ? "" : "Exemple de règle non respectée"; // À remplacer par la vraie règle
            validationOutputs.add(new ValidationOutput(field, result.isValid(), result.getMessage(), ruleViolated));
        });

        // Affichage des résultats
        System.out.println("------------RESULTAT------------------");
        validationOutputs.forEach(System.out::println);*/

    }
}
