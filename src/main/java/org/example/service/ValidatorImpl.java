package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ValidatorImpl  implements IValidator {

    private IRuleManager ruleManager;

    public ValidatorImpl() {
    }

    @Override
    public void start(ValidatorParam param) throws IOException {
        this.ruleManager = RuleManager.getInstance();
        this.ruleManager.configure(param.getRuleFile());
    }

    @Override
    public DataObject convertJsonToDataObject(String jsonDataFile) {
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
    public Map<String, ValidationResult> validate(DataObject dataToValidate) throws Exception {
        if(this.ruleManager == null) {
            throw new Exception("Validateur non démarré");
        }
        return this.ruleManager.validate(dataToValidate);
    }



}
