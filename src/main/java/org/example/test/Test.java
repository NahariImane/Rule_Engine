package org.example.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.*;
import org.example.service.ValidatorImpl;

import java.io.File;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws Exception {

        IValidator myValidator = new ValidatorImpl();
        ValidatorParam param = new ValidatorParam("src/main/Configuration/RulesTest.xlsx", ValidatorTypeEnum.VIEW);
        myValidator.start(param);

        ObjectMapper objectMapper = new ObjectMapper();
        DataObject dataObject = objectMapper.readValue(new File("src/main/java/org/example/test/dataTest.json"), DataObject.class);
        Map<String, WorkflowValidationResult> validationResponse = myValidator.validate(dataObject);

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
