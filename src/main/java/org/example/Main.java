package org.example;

import org.example.model.Workflow;
import org.example.service.RuleEngine;
import org.example.service.RuleValidator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        try {
            // Charger les workflows depuis le fichier Excel
            RuleEngine ruleEngine = new RuleEngine("src/main/Configuration/RulesTest.xlsx");
            RuleValidator ruleValidator = new RuleValidator();

            // Données de test pour chaque workflow
            Map<String, Object> requestData1 = new HashMap<>();
            requestData1.put("CHAMP_DEMANDE_TYPE", "demande");
            requestData1.put("CHAMP_Type_TITRE", "CNIE");
            requestData1.put("CHAMP_DATE_NAISSANCE", "2010-01-01"); // moins de 18 ans

            Map<String, Object> requestData2 = new HashMap<>();
            requestData2.put("CHAMP_DEMANDE_TYPE", "demande");
            requestData2.put("CHAMP_Type_TITRE", "PSP");
            requestData2.put("CHAMP_DATE_NAISSANCE", "1990-01-01"); // 18 ans ou plus
            requestData2.put("CHAMP_ADRESSE", "123 Main St");

            Map<String, Object> requestData3 = new HashMap<>();
            requestData3.put("CHAMP_DEMANDE_TYPE", "demande");
            requestData3.put("CHAMP_Type_TITRE", "PSP");
            requestData3.put("CHAMP_DATE_NAISSANCE", "2010-01-01"); // moins de 18 ans

            // Validation pour DemandeCNIe mineur
            System.out.println("\n--- Validation du workflow : DemandeCNIe mineur ---");
            Workflow workflow1 = ruleEngine.getWorkflow("DemandeCNIe mineur");
            boolean isValid1 = ruleValidator.validateWorkflow(workflow1, requestData1);
            System.out.println("Le workflow DemandeCNIe mineur est " + (isValid1 ? "valide." : "invalide."));

            // Validation pour Demande passeport adulte
            System.out.println("\n--- Validation du workflow : Demande passeport adulte ---");
            Workflow workflow2 = ruleEngine.getWorkflow("Demande passeport adulte");
            boolean isValid2 = ruleValidator.validateWorkflow(workflow2, requestData2);
            System.out.println("Le workflow Demande passeport adulte est " + (isValid2 ? "valide." : "invalide."));

            // Validation pour Demande passeport mineur
            System.out.println("\n--- Validation du workflow : Demande passeport mineur ---");
            Workflow workflow3 = ruleEngine.getWorkflow("Demande passeport mineur");
            boolean isValid3 = ruleValidator.validateWorkflow(workflow3, requestData3);
            System.out.println("Le workflow Demande passeport mineur est " + (isValid3 ? "valide." : "invalide."));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}