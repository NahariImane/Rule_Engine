package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exception.RuleLoadingException;
import org.example.model.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ValidatorImpl  implements IValidator {

    private IRuleManager ruleManager;

    private ValidatorTypeEnum validatorType;

    public ValidatorImpl() {
    }


    @Override
    public void start(ValidatorParam param) throws IOException, RuleLoadingException {
        this.validatorType = param.getType();
        this.ruleManager = RuleManager.getInstance();
        this.ruleManager.configure(param.getRuleFile());
    }


    private DataObject convertJsonToDataObject(String jsonDataFile) {
        DataObject data = new DataObject();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Lire le fichier JSON
            JsonNode rootNode = objectMapper.readTree(new File(jsonDataFile));

            // Parcourir les champs du JSON et les ajouter dans DataObject
            rootNode.fields().forEachRemaining(field -> {
                String fieldName = field.getKey();
                String value = field.getValue().asText(); // Récupère la valeur en tant que texte
                data.addField(fieldName, value);
            });
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la lecture du fichier JSON : " + jsonDataFile);
        }

        return data;
    }



//    @Override
//    public Map<String, WorkflowValidationResult> validate(DataObject dataToValidate) throws Exception {
//
//        if (this.ruleManager == null) {
//            throw new Exception("Validateur non démarré");
//        }
//        Map<String, WorkflowValidationResult> result = this.ruleManager.validate(dataToValidate);
//
//        if(this.validatorType.equals(ValidatorTypeEnum.VIEW)) {
//            //Affichage des données
//            System.out.println("------------DATA FROM DataObject------------------");
//            for (Map.Entry<String, String> entry : dataToValidate.getFields().entrySet()) {
//                System.out.println(entry.getKey() + " : " + entry.getValue());
//            }
//
//            if(!result.isEmpty()) {
//                // Affichage des workflow identifié
//                System.out.println("------------VALIDATION------------------");
//                for (Map.Entry<String, WorkflowValidationResult> entry : result.entrySet()) {
//                    String workflow = entry.getKey();
//                    System.out.println("Workflow : " + workflow );
//                }
//
//                // Affichage des résultats de validation
//                System.out.println("------------RESULTAT------------------");
//                for (Map.Entry<String, WorkflowValidationResult> entry : result.entrySet()) {
//                    String workflow = entry.getKey();
//                    WorkflowValidationResult fieldsResult = entry.getValue();
//                    System.out.println(workflow + " : " + (fieldsResult.isValid() ? "Valide" : "Invalide"));
//
//                    for (Map.Entry<String, FieldValidationResult> e : fieldsResult.getFieldsResult().entrySet()) {
//                        String field = e.getKey();
//                        FieldValidationResult fieldResult = e.getValue();
//                        System.out.println("  " + field + " : " + (fieldResult.isValid() ? "Valide" : "Invalide"));
//                    }
//                }
//            }
//            else{
//                System.out.println("Aucun workflow identifié.");
//            }
//        }
//
//        return result;
//    }

    @Override
    public WorkflowValidationResult validate(DataObject dataToValidate) throws Exception {

        if (this.ruleManager == null) {
            throw new Exception("Validateur non démarré");
        }
        WorkflowValidationResult result = this.ruleManager.validate(dataToValidate);

        if(this.validatorType.equals(ValidatorTypeEnum.VIEW)) {
            //Affichage des données
            System.out.println("------------DATA FROM DataObject------------------");
            for (Map.Entry<String, String> entry : dataToValidate.getFields().entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }

            if(result != null) {
                // Affichage des workflow identifié
                System.out.println("------------VALIDATION------------------");

                    System.out.println("Workflow : " + result.getWorkflowName() );


                // Affichage des résultats de validation
                System.out.println("------------RESULTAT------------------");
                System.out.println(result.getWorkflowName() + " : " + (result.isValid() ? "Valide" : "Invalide"));

                for (Map.Entry<String, FieldValidationResult> entry : result.getFieldsResult().entrySet()) {
                    String field = entry.getKey();
                    FieldValidationResult fieldsResult = entry.getValue();
                    System.out.println(field + " : " + (fieldsResult.isValid() ? "Valide" : "Invalide"));
                }

                // Affichage des résultats de validation invalide
//                System.out.println("------------RESULTAT INVALIDE------------------");
//
//                for (Map.Entry<String, FieldValidationResult> entry : result.getFieldsInvalidResult().entrySet()) {
//                    String field = entry.getKey();
//                    FieldValidationResult fieldsResult = entry.getValue();
//                    System.out.println(field + " : " + (fieldsResult.isValid() ? "Valide" : "Invalide"));
//                }
            }
            else{
                System.out.println("Aucun workflow identifié.");
            }
        }

        return result;
    }



}
