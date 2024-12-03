package org.example.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.ValidationFunctions.*;
import org.example.model.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class RuleManager implements IRuleManager {
    private List<RuleContainer> ruleContainerList; // Liste de tous les containers chargés
    private String ruleFile;

    private static final RuleManager INSTANCE = new RuleManager();

    public static RuleManager getInstance() {
        return INSTANCE;
    }

    /************************************Load rule***********************************/
    @Override
    public void configure(String ruleFilePath) throws IOException {
        this.ruleFile = ruleFilePath;
        this.loadRules();
    }

    private void loadRules() throws IOException{

        if(ruleContainerList == null) {
            ruleContainerList = new ArrayList<>();
        }

        try (FileInputStream file = new FileInputStream(ruleFile);
             Workbook workbook = new XSSFWorkbook(file)) {

            // Lecture de la première feuille du fichier Excel
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Ignorer l'en-tête

                // Lire le workflow et sa condition
                Workflow workflow = parseWorkflow(row);

                // Lire les champs et les règles associées
                List<Rule> rules = new ArrayList<>();
                for (int col = 2; col < headerRow.getLastCellNum(); col += 3) {
                    Field field = parseField(row, headerRow, col);
                    if (field != null) {
                        Rule rule = parseRule(row, headerRow, col,field);
                        if (rule != null) {
                            rules.add(rule);
                        }
                    }
                }

                // Ajouter un RuleContainer avec le workflow et ses règles
                RuleContainer container = new RuleContainer(workflow, rules);
                ruleContainerList.add(container);
            }
        }
//        this.printRule();
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
        fieldName = fieldName.replace("CHAMP_","");

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

    private Rule parseRule(Row row, Row headerRow, int col, Field field) {
        // Lecture des cellules dans la colonne pour le champ et la règle
        Cell champCell = row.getCell(col);
        Cell ruleCell = row.getCell(col + 1); // Colonne contenant l'expression de la règle

        // Vérifier que la colonne "CHAMP" est marquée "YES" et que la règle est définie
        if (champCell != null && "YES".equalsIgnoreCase(champCell.getStringCellValue().trim()) && ruleCell != null) {
            // Récupérer l'expression  de la règle (entière)
            String ruleExpression = ruleCell.getStringCellValue().trim();

            // Retourner une instance de Rule et son expression
            Rule rule = new Rule();
            rule.setField(field);
            rule.setExpression(ruleExpression);
            return rule;
        }

        return null; // Retourner null si aucune règle n'est valide
    }

    private void printRule(){
        for (RuleContainer ruleConainer : this.ruleContainerList){
            System.out.println("workflow : " + ruleConainer.getWorkflow().getName());
            System.out.println("  condition : " + ruleConainer.getWorkflow().getCondition());
            for(Rule rule : ruleConainer.getRuleList()){
                System.out.println("    champ : " + rule.getField().getLabel());
                System.out.println("    regle : " + rule.getExpression());
            }
        }
    }



    /****************************************Valide***********************************/

    @Override
    public Map<String, ValidationResult> validate(Map<String, String> fieldsToValidate) throws Exception {

        DataObject input = new DataObject();
        fieldsToValidate.forEach(input::addField);

        Map<String, ValidationResult> results = new HashMap<>();

        List<RuleContainer> ruleContainers = this.findRules(fieldsToValidate);

        if(ruleContainers == null || ruleContainers.isEmpty()) {
            throw new Exception("Règles non chargées.");
        }

        for(RuleContainer ruleContainer: ruleContainers) {
            System.out.println("Workflow :" + ruleContainer.getWorkflow().getName());
            List<Rule> rules = ruleContainer.getRuleList();
            //complete la liste s'il y a des champs manquant avec null comme value
            Map<String,String> fieldsToValidateComplete = this.completeMissingField(fieldsToValidate,rules);
            rules.forEach(rule -> {
                ValidationResult result = new ValidationResult();

                //checher la regle correspondant à la valeur du champ et ajouter à fieldsToValidate
                // nouvelle key = "value" , value = valeur trouvée sinon null
                this.addFieldValueWithRule(rule,fieldsToValidate);

                boolean isValid = this.evaluateExpression(rule.getExpression(),fieldsToValidate);
                result.setValid(isValid);
                result.setMessage(isValid ? null : "Non valid");
                results.put(rule.getField().getLabel(), result);

            });
        }

        return results;
    }

    private Map<String,String> completeMissingField(Map<String,String> fieldsToValidate,List<Rule> rules){
        Map<String,String> result = new HashMap<>(fieldsToValidate);
        for(Rule rule : rules){
            fieldsToValidate.putIfAbsent(rule.getField().getLabel(),null);
        }
        return result;
    }

    public List<RuleContainer> findRules(Map<String, String> fieldsToValidate) {
        return this.ruleContainerList.stream()
                .filter(ruleContainer -> this.evaluateExpression(ruleContainer.getWorkflow().getCondition(), fieldsToValidate))
                .collect(Collectors.toList());
    }

    private void addFieldValueWithRule(Rule rule, Map<String, String> fieldsToValidate)  {
        String value = null;
        for (Map.Entry<String, String> entry : fieldsToValidate.entrySet()) {
            if(entry.getKey().equals(rule.getField().getLabel())){
                value = entry.getValue();
            }
        }
        fieldsToValidate.put("value",value);
    }



    public boolean evaluateExpression(String condition, Map<String, String> fieldsToValidate) {
        //TODO: evaluate expression
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

        Bindings bindings = engine.createBindings();
        bindings.putAll(fieldsToValidate);
        bindings.put("Valid_Date", new Valid_Date());
        bindings.put("Major_Check", new Major_Check());
        bindings.put("Minor_Check", new Minor_Check());
        bindings.put("Length_Between", new Length_Between());
        bindings.put("IsValidName", new IsValidName());
        bindings.put("BornInFrance", new BornInFrance());
        bindings.put("IsNumber", new IsNumber());
        bindings.put("IsValidTaille", new IsValidTaille());
        bindings.put("Length_Less_Than", new Length_Less_Than());
        bindings.put("Length_Greater_Than", new Length_Greater_Than());
        bindings.put("BelongTo", new BelongTo());
        bindings.put("NotNull", new NotNull());
        bindings.put("IsNull", new IsNull());


//        System.out.println("value : "  + fieldsToValidate.get("value"));
//        System.out.println("condition : " + condition);

        try {
            Boolean r = (Boolean) engine.eval(condition, bindings);
            return r;
        } catch (ScriptException e) {
            System.err.println("Erreur lors de l'évaluation de l'expression : " + condition);
            e.printStackTrace();
            return false;
        }
    }



}
