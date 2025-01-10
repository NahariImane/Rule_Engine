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

    private ValidatorParam validatorParam;

    public ValidatorImpl() {
    }


    @Override
    public void start(ValidatorParam param) throws IOException, RuleLoadingException {
        this.validatorParam = param;
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



    @Override
    public WorkflowValidationResult validate(DataObject dataToValidate) throws Exception {

        if (this.ruleManager == null) {
            throw new Exception("Validateur non démarré");
        }
        WorkflowValidationResult result = this.ruleManager.validate(dataToValidate);


            //Affichage des données
            System.out.println("------------DATA FROM DataObject------------------");
            for (Map.Entry<String, String> entry : dataToValidate.getFields().entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }

            if(result != null) {
                // Affichage des workflow identifié
                System.out.println("------------VALIDATION------------------");
                    for(String s : result.getWorkflowNames()){
                        System.out.println("Workflow : " + s );
                    }


            // Affichage des résultats de validation
            System.out.println("------------RESULTAT------------------");
            System.out.println("Résultat global : " + (result.isValid() ? "Valide" : "Invalide"));

            for (Map.Entry<String, FieldValidationResult> entry : result.getFieldsResult().entrySet()) {
                String field = entry.getKey();
                FieldValidationResult fieldsResult = entry.getValue();
                System.out.println(field + " : " + (fieldsResult.isValid() ? "Valide" : "Invalide"));
            }

            // Affichage des résultats de validation invalide
//            System.out.println("------------RESULTAT INVALIDE------------------");
//
//            for (Map.Entry<String, FieldValidationResult> entry : result.getFieldsInvalidResult().entrySet()) {
//                String field = entry.getKey();
//                FieldValidationResult fieldsResult = entry.getValue();
//                System.out.println(field + " : " + (fieldsResult.isValid() ? "Valide" : "Invalide"));
//            }
        }
        else{
            System.out.println("Aucun workflow identifié.");
        }


        return result;
    }



}
