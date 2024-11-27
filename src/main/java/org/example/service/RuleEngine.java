package org.example.service;

import com.sun.corba.se.spi.orbutil.threadpool.Work;
import org.example.ValidationFunctions;
import org.example.model.*;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mvel2.MVEL;

import javax.xml.crypto.Data;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RuleEngine {

    private RuleManager ruleManager;

    public RuleEngine(RuleManager ruleManager) {

        this.ruleManager = ruleManager;
    }

    public void start(Map<String, Object> inputData) {
        // Identifie le workflow applicable en fonction des conditions
        List<RuleContainer> containers = ruleManager.identifyWorkflows(inputData);

        if (containers.isEmpty()) {
            System.out.println("Aucun workflow applicable.");
            return;
        }

        // Affiche les workflows identifiés
        System.out.println("Workflows applicables identifiés : ");
        for (RuleContainer container : containers) {
            System.out.println("- " + container.getWorkflow().getName());
            for (Field field : container.getFields()) {
                validateField(container, field, inputData);
            }
        }
    }
    private List<Field> collectFieldsFromWorkflows(List<RuleContainer> containers) {
        // Collecte unique des champs de tous les workflows applicables
        Set<String> seenFields = new HashSet<>();
        List<Field> fields = new ArrayList<>();

        for (RuleContainer container : containers) {
            for (Field field : container.getFields()) {
                if (seenFields.add(field.getLabel())) {
                    fields.add(field);
                }
            }
        }

        return fields;
    }
    private Map<String, List<Rule>> mergeRulesFromWorkflows(List<RuleContainer> containers) {
        // Fusionner les règles des workflows applicables
        Map<String, List<Rule>> mergedRules = new HashMap<>();

        for (RuleContainer container : containers) {
            for (Rule rule : container.getRules()) {
                mergedRules
                        .computeIfAbsent(rule.getId(), k -> new ArrayList<>())
                        .add(rule);
            }
        }

        return mergedRules;
    }


    private void validateField(RuleContainer container, Field field, Map<String, Object> inputData) {
        Object value = inputData.get(field.getLabel());

        // Vérifie si le champ est obligatoire et si la valeur est présente
        if (field.isObligatory() && value == null) {
            System.out.println("Erreur : Le champ obligatoire '" + field.getLabel() + "' est absent.");
            return;
        }

        // Trouve la règle pour ce champ
        Rule rule = container.getRules().stream()
                .filter(r -> r.getId().equalsIgnoreCase(field.getLabel()))
                .findFirst()
                .orElse(null);

        if (rule == null) {
            System.out.println("Aucune règle pour le champ : " + field.getLabel());
            return;
        }

        // Vérifie les conditions de la règle
        for (Condition condition : rule.getConditions()) {
            boolean result = evaluateCondition(condition.getExpression(), inputData);
            System.out.println("Condition : " + condition.getExpression() + " -> " + result);
            if (!result) {
                System.out.println("Erreur : Validation échouée pour le champ : " + field.getLabel());
                return;
            }
        }

        // Validation réussie pour ce champ
        System.out.println("Validation réussie pour le champ : " + field.getLabel());
    }

    private boolean isTypeCompatible(Object value, String expectedType) {
        switch (expectedType.toLowerCase()) {
            case "string":
                return value instanceof String;
            case "integer":
            case "int":
                return value instanceof Integer;
            case "double":
                return value instanceof Double;
            case "date":
                return value instanceof java.util.Date;
            default:
                return true; // Par défaut, considérer compatible si le type n'est pas défini
        }
    }



    private boolean evaluateCondition(String condition, Map<String, Object> inputData) {
        // Remplace les variables dans la condition par leurs valeurs
        for (Map.Entry<String, Object> entry : inputData.entrySet()) {
            String placeholder = "%" + entry.getKey() + "%";
            condition = condition.replace(placeholder, "\"" + entry.getValue().toString() + "\"");
        }

        // Crée le contexte d'exécution pour MVEL
        Map<String, Object> context = new HashMap<>();
        context.put("Minor_Check", (BiFunction<String, Integer, Boolean>) ValidationFunctions::Minor_Check);

        try {
            // Évalue la condition avec MVEL
            return (Boolean) MVEL.eval(condition, context);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'évaluation de la condition : " + condition);
            e.printStackTrace();
            return false;
        }
    }
}
