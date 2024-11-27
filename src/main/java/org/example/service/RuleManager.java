package org.example.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.model.*;
import org.mvel2.MVEL;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RuleManager {
    private List<RuleContainer> ruleContainers; // Liste de tous les containers chargés

    public RuleManager() {
        this.ruleContainers = new ArrayList<>();
    }

    // Méthode pour charger les données depuis Excel

    // Charge les workflows, champs et règles depuis un fichier Excel
    public void loadFromExcel(String filePath) throws IOException {
        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            // Lecture de la première feuille du fichier Excel
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Ignorer l'en-tête

                // Lire le workflow et sa condition
                Workflow workflow = parseWorkflow(row);

                // Lire les champs et les règles associées
                List<Field> fields = new ArrayList<>();
                List<Rule> rules = new ArrayList<>();
                for (int col = 2; col < headerRow.getLastCellNum(); col += 3) {
                    Field field = parseField(row, headerRow, col);
                    if (field != null) {
                        fields.add(field);
                        Rule rule = parseRule(row, headerRow, col);
                        if (rule != null) {
                            rules.add(rule);
                        }
                    }
                }

                // Ajouter un RuleContainer avec le workflow, ses champs et ses règles
                RuleContainer container = new RuleContainer(workflow, fields, rules);
                ruleContainers.add(container);
            }
        }
    }

    // Méthode pour parser un Workflow
    private Workflow parseWorkflow(Row row) {
        String workflowName = row.getCell(0).getStringCellValue().trim(); // Colonne Workflow Name
        String condition = row.getCell(1).getStringCellValue().trim();    // Colonne Condition
        return new Workflow(workflowName, condition);
    }




    private Field parseField(Row row, Row headerRow, int col) {
        // Récupérer la cellule contenant "YES" ou "NO"
        Cell champCell = row.getCell(col);

        // Initialiser les valeurs par défaut
        String fieldName = headerRow.getCell(col).getStringCellValue().trim(); // Nom du champ
        boolean isObligatory = false; // Valeur par défaut : non obligatoire
        String fieldType = ""; // Type par défaut : vide

        // Vérification de l'état "YES" ou "NO"
        if (champCell != null) {
            String cellValue = champCell.getStringCellValue().trim();
            isObligatory = "YES".equalsIgnoreCase(cellValue); // Définir si obligatoire
        }

        // Lecture du type depuis la colonne TYPE_... (col+2)
        Cell typeCell = row.getCell(col + 2);
        if (typeCell != null) {
            fieldType = typeCell.getStringCellValue().trim();
        }

        // Créer et retourner une instance de Field
        return new Field(fieldName, fieldType, isObligatory);
    }



    private List<Condition> parseConditions(String ruleExpression) {
        // Liste pour stocker les objets Condition
        List<Condition> conditions = new ArrayList<>();

        // Séparer l'expression en sous-conditions en utilisant "&&" ou "||" comme séparateurs
        // Utilisation de regex pour détecter "&&" et "||" comme séparateurs
        String[] conditionParts = ruleExpression.split("\\s*(&&|\\|\\|)\\s*");

        // Créer un objet Condition pour chaque partie et l'ajouter à la liste
        for (String conditionPart : conditionParts) {
            conditions.add(new Condition(conditionPart.trim()));
        }

        return conditions;
    }



    private Rule parseRule(Row row, Row headerRow, int col) {
        // Lecture des cellules dans la colonne pour le champ et la règle
        Cell champCell = row.getCell(col);
        Cell ruleCell = row.getCell(col + 1); // Colonne contenant l'expression de la règle

        // Vérifier que la colonne "CHAMP" est marquée "YES" et que la règle est définie
        if (champCell != null && "YES".equalsIgnoreCase(champCell.getStringCellValue().trim()) && ruleCell != null) {
            // Récupérer l'identifiant de la règle à partir de l'en-tête
            String ruleId = headerRow.getCell(col+1).getStringCellValue().trim();

            // Récupérer l'expression  de la règle (entière)
            String ruleExpression = ruleCell.getStringCellValue().trim();

            // Découper l'expression en conditions individuelles
            List<Condition> parsedConditions = parseConditions(ruleExpression);

            // Retourner une instance de Rule avec son ID et ses conditions
            return new Rule(ruleId, parsedConditions);
        }
        return null; // Retourner null si aucune règle n'est valide
    }




    // Retourne les containers chargés
    public List<RuleContainer> getRuleContainers() {
        return ruleContainers;
    }
    public RuleContainer identifyWorkflow(Map<String, Object> inputData) {
        for (RuleContainer container : ruleContainers) {
            if (evaluateCondition(container.getWorkflow().getCondition(), inputData)) {
                return container;
            }
        }
        return null;
    }

    // Nouvelle méthode pour identifier tous les workflows applicables
    public List<RuleContainer> identifyWorkflows(Map<String, Object> inputData) {
        List<RuleContainer> applicableContainers = new ArrayList<>();
        for (RuleContainer container : ruleContainers) {
            if (evaluateCondition(container.getWorkflow().getCondition(), inputData)) {
                applicableContainers.add(container);
            }
        }
        return applicableContainers;
    }


    private boolean evaluateCondition(String condition, Map<String, Object> inputData) {
        try {
            // Remplacez les placeholders (%CHAMP_XXX%) par les valeurs correspondantes
            for (Map.Entry<String, Object> entry : inputData.entrySet()) {
                condition = condition.replace("%" + entry.getKey() + "%", "\"" + entry.getValue().toString() + "\"");
            }

            // Utilisation de MVEL pour évaluer l'expression
            return (Boolean) MVEL.eval(condition);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'évaluation de la condition : " + condition);
            e.printStackTrace();
            return false;
        }
    }
}
