package org.example;

import org.example.model.*;
import org.example.service.RuleEngine;
import org.example.service.RuleManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {


        // Initialiser RuleManager
        RuleManager ruleManager = new RuleManager();

        // Charger les données depuis un fichier Excel
        String excelFilePath = "src/main/Configuration/RulesTest.xlsx";
        try {
            ruleManager.loadFromExcel(excelFilePath);
            System.out.println("Données chargées avec succès depuis : " + excelFilePath);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du fichier Excel : " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Vérifier que les données ont bien été chargées
        List<RuleContainer> ruleContainers = ruleManager.getRuleContainers();
        if (ruleContainers.isEmpty()) {
            System.out.println("Aucun container de règles chargé. Assurez-vous que le fichier Excel contient des données.");
            return; // Arrêter l'exécution
        } else {
            System.out.println("Nombre de containers chargés : " + ruleContainers.size());
            displayLoadedData(ruleContainers);
        }

        //  Initialiser RuleEngine
        RuleEngine ruleEngine = new RuleEngine(ruleManager);


        Map<String, Object> inputData = new HashMap<>();
        inputData.put("CHAMP_PRENOM", "Imane");
        inputData.put("CHAMP_DATE_NAISSANCE", "2001-09-22");
        inputData.put("CHAMP_TYPE_TITRE", "PSP");

        // Démarrer la validation
        ruleEngine.start(inputData);
    }
    private static void displayLoadedData(List<RuleContainer> ruleContainers) {
        System.out.println("=== Contenu chargé ===");
        for (RuleContainer container : ruleContainers) {
            System.out.println("Workflow : " + container.getWorkflow().getName());
            System.out.println("Condition du workflow : " + container.getWorkflow().getCondition());


            System.out.println("Champs :");
            for (Field field : container.getFields()) {
                System.out.println("  - " + field.getLabel() + " (Type : " + field.getType() + ", Obligatoire : " + field.isObligatory() + ")");
            }

            System.out.println("Règles :");
            for (Rule rule : container.getRules()) {
                System.out.println("  - Règle ID : " + rule.getId());
                System.out.println("    Conditions :");
                for (Condition condition : rule.getConditions()) {
                    System.out.println("      * " + condition.getExpression());
                }
            }

            System.out.println("-------------------------------");
        }


    }




}
