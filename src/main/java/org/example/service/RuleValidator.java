package org.example.service;

import org.example.model.Rule;
import org.example.model.Workflow;
import org.mvel2.MVEL;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

public class RuleValidator {

    // Méthode de validation pour un workflow et les données de la requête
    public boolean validateWorkflow(Workflow workflow, Map<String, Object> request) {
        boolean isWorkflowValid = true;

        for (Rule rule : workflow.getRules()) {
            String field = rule.getField();
            String expression = preprocessExpression(rule.getExpression(), request);

            // Créer un contexte pour l'évaluation
            Map<String, Object> context = new HashMap<>(request);
            context.put("value", request.get(field));

            try {
                boolean isValid = MVEL.evalToBoolean(expression, context);
                if (!isValid) {
                    System.out.println("Erreur de validation pour le champ '" + field + "' avec l'expression '" + expression + "'");
                    isWorkflowValid = false;
                }
            } catch (Exception e) {
                System.out.println("Erreur d'évaluation pour le champ '" + field + "' avec l'expression '" + expression + "': " + e.getMessage());
                isWorkflowValid = false;
            }
        }
        return isWorkflowValid;
    }

    // Prétraitement pour transformer "age < 18" en une expression MVEL calculable
    private String preprocessExpression(String expression, Map<String, Object> request) {
        if (expression.contains("age")) {
            Object birthDateObj = request.get("CHAMP_DATE_NAISSANCE");
            if (birthDateObj instanceof String) {
                LocalDate birthDate = LocalDate.parse((String) birthDateObj);
                int age = Period.between(birthDate, LocalDate.now()).getYears();
                expression = expression.replace("age", String.valueOf(age));
            }
        }
        return expression;
    }
}
