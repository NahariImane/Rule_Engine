package org.example;

import org.example.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {



        IValidator myValidator = new ValidatorImpl();
        ValidatorParam param = new ValidatorParam("src/main/Configuration/RulesTest.xlsx", ValidatorTypeEnum.DATA);
        myValidator.start(param);

        DataObject input = new DataObject();


        System.out.println("********* using Dynamic Data object **************");
        input.addField("PRENOM","Imane");
        input.addField("NOM", "NAHARI");
        input.addField("DATE_NAISSANCE", "22/09/2001");
        input.addField("STATUT", "majeur");
        input.addField("TYPE_TITRE", "PSP");
        input.addField("SEXE", "femme");
        input.addField("PAYS_NAISSANCE", "france");
        input.addField("DEPARTEMENT_NAISSANCE", "91");
        input.addField("TAILLE", "1.63");

        // Affichage des données ajoutées dans DataObject
        System.out.println("------------DATA FROM DataObject------------------");
        for (Map.Entry<String, Object> entry : input.getFields().entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        // Conversion de DataObject en Map<String, String> pour la validation
        Map<String, String> fieldsToValidate = new HashMap<>();
        input.getFields().forEach((key, value) -> fieldsToValidate.put(key, value.toString()));




        /*System.out.println("------------DATA------------------");
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
        fieldsToValidate.put("DEPARTEMENT_NAISSANCE", "011");*/

        /*for(Map.Entry<String, String> entry : fieldsToValidate.entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();

            System.out.println(field + " : " + value);
        }*/

        System.out.println("------------VALIDATION------------------");
        Map<String, ValidationResult> validationResponse = myValidator.validate(fieldsToValidate);

       /* System.out.println("------------RESULTAT------------------");
        for(Map.Entry<String, ValidationResult> entry : validationResponse.entrySet()) {
            String field = entry.getKey();
            ValidationResult result = entry.getValue();

            System.out.println(field + " : " + result.isValid());
        }*/

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
