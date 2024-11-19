package org.example.service;

import com.sun.corba.se.spi.orbutil.threadpool.Work;
import org.example.ValidationFunctions;
import org.example.model.DataObject;
import org.example.model.Rule;
import org.example.model.Workflow;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mvel2.MVEL;

import javax.xml.crypto.Data;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RuleEngine {
    public Map<String , Workflow> workflows;
    public RuleEngine(String excelFilePath)throws IOException {
        this.workflows = loadRulesFromExcel(excelFilePath);
        System.out.println("Workflows chargés :");
        workflows.forEach((name, workflow) -> {
            System.out.println("Nom du workflow : " + name);
            workflow.getRules().forEach(rule ->
                    System.out.println(" - Champ : " + rule.getField() + ", Expression : " + rule.getExpression()));
        });
    }

    private Map<String , Workflow> loadRulesFromExcel (String filePath) throws  IOException{
        Map<String,Workflow> workflows = new HashMap<>();
        try(FileInputStream file = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook (file)){
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            for (Row row : sheet){
                if( row.getRowNum() == 0) continue;
                    // ----------------Format vertical -----------------------
                /*String context = row.getCell(0).getStringCellValue().trim();
                String field = row.getCell(1).getStringCellValue().trim();
                String expression = row.getCell(4).getStringCellValue().trim();

                Rule rule = new Rule(field, expression);
                workflows.computeIfAbsent(context, k -> new Workflow(context, new ArrayList<>())).getRules().add(rule);
            */
                // Lecture des informations générales du workflow
                String workflowName = row.getCell(0).getStringCellValue().trim();
                String condition = row.getCell(1).getStringCellValue().trim();

                Workflow workflow = new Workflow(workflowName,condition, new ArrayList<>());
                // Parcourir les colonnes par paires à partir de la troisième colonne
                for (int col = 2; col < headerRow.getLastCellNum(); col += 2) {
                    Cell champCell = row.getCell(col);
                    Cell expressionCell = row.getCell(col + 1);
                    // Vérification si le champ est activé ("YES") et que l'expression est présente
                    if (champCell != null && "YES".equalsIgnoreCase(champCell.getStringCellValue().trim()) && expressionCell != null) {
                        // Nom du champ basé sur l'en-tête
                        String fieldName = headerRow.getCell(col).getStringCellValue().trim();
                        String expression = expressionCell.getStringCellValue().trim();
                        // Création de la règle et ajout au workflow
                        Rule rule = new Rule(fieldName, expression);
                        workflow.getRules().add(rule);
                    }
                }
                //Ajout du workflow au map :
                workflows.put(workflowName, workflow);
            }
        }
    return workflows;
    }

    private boolean evaluateCondition(String condition, Map<String, Object> context) {
        try {
            // Ajouter la fonction Minor_Check au contexte
            context.put("Minor_Check", ValidationFunctions.class.getMethod("Minor_Check", String.class, int.class));

            // Évaluer la condition avec MVEL
            return (Boolean) MVEL.eval(condition, context);
        } catch (Exception e) {
            System.err.println("Erreur d'évaluation de la condition : " + condition);
            return false;
        }
    }

    private String substitutePlaceholders(String condition, DataObject object) {
        // Substitution des placeholders
        for (String field : object.getFields().keySet()) {
            String placeholder = "%" + field + "%";
            String value = object.getField(field) == null ? "null" : "'" + object.getField(field).toString() + "'";
            condition = condition.replace(placeholder, value);
        }
        return condition;
    }



    public Workflow identifyWorkflow(DataObject object) {
        for (Workflow workflow : workflows.values()) {
            // Substituer les placeholders dans la condition
            String preparedCondition = substitutePlaceholders(workflow.getCondition(), object);

            // Préparer le contexte pour MVEL
            Map<String, Object> context = new HashMap<>();
            try {
                context.put("Minor_Check", ValidationFunctions.class.getMethod("Minor_Check", String.class, int.class));
            } catch (Exception e) {
                System.err.println("Erreur lors de l'ajout de la fonction Minor_Check au contexte : " + e.getMessage());
            }

            // Évaluer la condition
            if (evaluateCondition(preparedCondition, context)) {
                // Étape 3 : Valider les règles des champs du workflow
                if (workflow.validateField("CHAMP_TYPE_TITRE", object.getField("CHAMP_TYPE_TITRE"))) {
                    //System.out.println("Workflow identifié : " + workflow.getName());
                    return workflow;
                }/* else {
                    System.out.println("Condition remplie, mais le champ CHAMP_TYPE_TITRE ne correspond pas pour le workflow : " + workflow.getName());
                }*/
            }
        }
        return null;
    }

}
