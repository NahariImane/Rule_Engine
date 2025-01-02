package org.example.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.exception.RuleLoadingException;
import org.example.exception.RuleValidationException;
import org.example.model.ValidationFunctions.*;
import org.example.model.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleManager implements IRuleManager {
    private List<RuleContainer> ruleContainerList; // Liste de tous les containers chargés
    private String ruleFile;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    public String getRuleFile() {
        return ruleFile;
    }


    private static final RuleManager INSTANCE = new RuleManager();

    public static RuleManager getInstance() {
        return INSTANCE;
    }

    /************************************Load rule***********************************/
    @Override
    public void configure(String ruleFilePath) throws IOException, RuleLoadingException {
        this.ruleFile = ruleFilePath;
        validateExcelStructure();
        this.loadRules();
    }

    void validateExcelStructure() throws RuleLoadingException {
        try (FileInputStream file = new FileInputStream(ruleFile);
             Workbook workbook = new XSSFWorkbook(file)) {

            // Vérifiez si le fichier contient au moins une feuille
            if (workbook.getNumberOfSheets() == 0) {
                throw new RuleLoadingException("Le fichier Excel ne contient aucune feuille.");
            }

            // Sélectionnez la première feuille
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                throw new RuleLoadingException("La première feuille du fichier Excel est vide.");
            }

            // Vérifiez si l'en-tête est présent
            Row headerRow = sheet.getRow(0);
            if (headerRow == null || headerRow.getLastCellNum() < 4) { // Minimum 4 colonnes pour respecter la structure
                throw new RuleLoadingException("L'en-tête du fichier Excel est manquant ou incomplet.");
            }

            // Vérifiez les colonnes obligatoires
            if (!"Workflow name".equalsIgnoreCase(headerRow.getCell(0).getStringCellValue().trim())) {
                throw new RuleLoadingException("La colonne 'Workflow name' est manquante ou incorrecte.");
            }
            if (!"Condition".equalsIgnoreCase(headerRow.getCell(1).getStringCellValue().trim())) {
                throw new RuleLoadingException("La colonne 'Condition' est manquante ou incorrecte.");
            }

            // Valider les colonnes dynamiques (à partir de la 3e colonne)
            for (int col = 2; col < headerRow.getLastCellNum(); col += 2) {
                // Lire les valeurs des colonnes
                String regle_header = headerRow.getCell(col) != null ? headerRow.getCell(col ).getStringCellValue().trim() : null;
                String message_header = headerRow.getCell(col + 1) != null ? headerRow.getCell(col + 1).getStringCellValue().trim() : null;

                // Vérifier si toutes les colonnes sont vides, ce qui signale la fin des colonnes dynamiques
                if ((regle_header == null || regle_header.isEmpty()) &&
                        (message_header == null || message_header.isEmpty())) {
                    break; // Fin des colonnes dynamiques
                }

                // Validation de la colonne REGLE_
                if (regle_header == null || !regle_header.startsWith("REGLE_")) {
                    throw new RuleLoadingException("Colonne invalide à l'index " + convertToColumnLetter(col ) + ": attendu 'REGLE_XXX'.");
                }

                // Validation de la colonne MESSAGE_
                if (message_header == null || !message_header.startsWith("MESSAGE_")) {
                    throw new RuleLoadingException("Colonne invalide à l'index " + convertToColumnLetter(col + 1) + ": attendu 'MESSAGE_XXX'.");
                }
            }
            // Parcourir les lignes du fichier Excel
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                // Ignorer les lignes nulles ou vides
                if (row == null || isRowEmpty(row)) {
                    continue; // next line
                }

                // Valider les valeurs des colonnes Workflow name et Condition
                String workflowName = getCellValue(row.getCell(0));
                String condition = getCellValue(row.getCell(1));

                if (workflowName == null || workflowName.isEmpty()) {
                    throw new RuleLoadingException("La colonne 'Workflow name' est vide à la ligne " + (rowIndex + 1));
                }

                if (condition == null || condition.isEmpty()) {
                    throw new RuleLoadingException("La colonne 'Condition' est vide à la ligne " + (rowIndex + 1));
                }

                // Parcourir les colonnes dynamiques (REGLE_ et MESSAGE_)
                for (int col = 2; col < headerRow.getLastCellNum(); col += 2) {
                    String regleHeader = getCellValue(headerRow.getCell(col));
                    String messageHeader = getCellValue(headerRow.getCell(col + 1));

                    if (regleHeader != null && regleHeader.startsWith("REGLE_")) {
                        String regleValue = getCellValue(row.getCell(col));
                        String messageValue = getCellValue(row.getCell(col + 1));

                        // Validation des valeurs des colonnes dynamiques
                        if ((regleValue == null || regleValue.isEmpty())) {
                            logger.warn("La colonne '{}' est vide à la ligne {}. Le champs sera considérée comme valide par défaut.", regleHeader, (rowIndex + 1));
                           // throw new RuleLoadingException("La colonne '" + regleHeader + "' est vide  à la ligne " + (rowIndex + 1));
                        }
                        if ((messageValue == null || messageValue.isEmpty()) ) {
                            logger.warn("La colonne '{}' est vide à la ligne {}.", messageHeader, (rowIndex + 1));
                            //throw new RuleLoadingException("La colonne '" + messageHeader + "' est vide à la ligne " + (rowIndex + 1));
                        }
                    }
                }
            }

        }catch (FileNotFoundException e) {
            logger.error("Fichier non trouvé : {}", e.getMessage());
            throw new RuleLoadingException("Le fichier spécifié est introuvable.", e);
        }catch (IOException e) {
            throw new RuleLoadingException("Erreur lors de l'ouverture du fichier Excel : " + e.getMessage(), e);
        }
    }

    // Méthode pour vérifier si une ligne est vide
    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK && cell.getStringCellValue() != null && !cell.getStringCellValue().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    // Méthode pour obtenir la valeur d'une cellule
    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue().trim() : null;
    }


    public static String convertToColumnLetter(int columnIndex) {
            StringBuilder columnLetter = new StringBuilder();
            columnIndex += 1; // Ajuster l'index pour commencer à 1.
            while (columnIndex > 0) {
                int remainder = (columnIndex - 1) % 26;
                columnLetter.insert(0, (char) (remainder + 'A'));
                columnIndex = (columnIndex - 1) / 26;
            }
            return columnLetter.toString();
        }

//    private void loadRules() throws IOException{
//
//        if(ruleContainerList == null) {
//            ruleContainerList = new ArrayList<>();
//        }
//
//        try (FileInputStream file = new FileInputStream(ruleFile);
//             Workbook workbook = new XSSFWorkbook(file)) {
//
//            // Lecture de la première feuille du fichier Excel
//            Sheet sheet = workbook.getSheetAt(0);
//            Row headerRow = sheet.getRow(0);
//
//            for (Row row : sheet) {
//                if (row.getRowNum() == 0) continue; // Ignorer l'en-tête
//
//                // Lire le workflow et sa condition
//                Workflow workflow = parseWorkflow(row);
//
//                // Lire les champs et les règles associées
//                List<Rule> rules = new ArrayList<>();
//                for (int col = 2; col < headerRow.getLastCellNum(); col += 3) {
//                    Field field = parseField(headerRow, col);
//                    if (field != null){
//                        Rule rule = parseRule(row, col, field);
//                        if (rule != null) {
//                            rules.add(rule);
//                        }
//                  }
//                }
//
//                // Ajouter un RuleContainer avec le workflow et ses règles
//                RuleContainer container = new RuleContainer(workflow, rules);
//                ruleContainerList.add(container);
//            }
//        }
////        this.printRules();
//    }
//
//    // Méthode pour parser un Workflow
//    private Workflow parseWorkflow(Row row) {
//        String workflowName = row.getCell(0).getStringCellValue().trim(); // Colonne Workflow Name
//        String condition = row.getCell(1).getStringCellValue().trim();    // Colonne Condition
//        return new Workflow(workflowName, condition);
//    }
//
//    private Field parseField(Row headerRow, int col) {
//        Cell fieldNameCell = headerRow.getCell(col);
//        if(fieldNameCell != null) {
//            // Initialiser les valeurs par défaut
//            String fieldName =fieldNameCell.getStringCellValue().trim(); // Nom du champ
//            fieldName = fieldName.replace("CHAMP_", "");
//
//            // Créer et retourner une instance de Field
//            return new Field(fieldName);
//        }
//        return null;
//    }
//
//    private Rule parseRule(Row row,  int col, Field field) {
//        // Lecture des cellules dans la colonne pour le champ et la règle
//        Cell champCell = row.getCell(col);
//        Cell ruleCell = row.getCell(col + 1); // Colonne contenant l'expression de la règle
//        Cell desciptionCell = row.getCell(col + 2); // Colonne contenant la description de la règle
//        // Vérifier que la colonne "CHAMP" est marquée "YES" et que la règle est définie
//        if (champCell != null && "YES".equalsIgnoreCase(champCell.getStringCellValue().trim()) && ruleCell != null && desciptionCell != null) {
//            // Récupérer l'expression de la règle (entière)
//            String ruleExpression = ruleCell.getStringCellValue().trim();
//            String ruleDescription = desciptionCell.getStringCellValue().trim();
//            // Retourner une instance de Rule et son expression et sa description
//            return new Rule(field,ruleExpression,ruleDescription);
//        }
//
//        return null; // Retourner null si aucune règle n'est valide
//    }

void loadRules() throws IOException{

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
            for (int col = 2; col < headerRow.getLastCellNum(); col += 2) {
                Field field = parseField(headerRow, col);
                if (field != null){
                    Rule rule = parseRule(row, col, field);
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
//        this.printRules();
}

    // Méthode pour parser un Workflow
    private Workflow parseWorkflow(Row row) {
        String workflowName = row.getCell(0).getStringCellValue().trim(); // Colonne Workflow Name
        String condition = row.getCell(1).getStringCellValue().trim();    // Colonne Condition
        return new Workflow(workflowName, condition);
    }

    private Field parseField(Row headerRow, int col) {
        Cell fieldNameCell = headerRow.getCell(col);
        if(fieldNameCell != null) {
            // Initialiser les valeurs par défaut
            String fieldName =fieldNameCell.getStringCellValue().trim(); // Nom du champ
            fieldName = fieldName.replace("REGLE_", "");

            // Créer et retourner une instance de Field
            return new Field(fieldName);
        }
        return null;
    }

    private Rule parseRule(Row row,  int col, Field field) {
        // Lecture des cellules dans la colonne pour le champ et la règle
        Cell ruleCell = row.getCell(col ); // Colonne contenant l'expression de la règle
        Cell desciptionCell = row.getCell(col + 1); // Colonne contenant la description de la règle

        if (ruleCell != null && desciptionCell != null) {
            // Récupérer l'expression de la règle (entière)
            String ruleExpression = ruleCell.getStringCellValue().trim();
            String ruleDescription = desciptionCell.getStringCellValue().trim();
            // Retourner une instance de Rule et son expression et sa description
            return new Rule(field,ruleExpression,ruleDescription);
        }

        return null; // Retourner null si aucune règle n'est valide
    }

    private void printRules(){
        for (RuleContainer ruleConainer : this.ruleContainerList){
            System.out.println("workflow : " + ruleConainer.getWorkflow().getName());
            System.out.println("  condition : " + ruleConainer.getWorkflow().getCondition());
            for(Rule rule : ruleConainer.getRuleList()){
                System.out.println("    champ : " + rule.getField().getLabel());
                System.out.println("    regle : " + rule.getExpression());
                System.out.println("    description : " + rule.getDescription());
            }
        }
    }



    /****************************************Validate***********************************/

//    @Override
//    public Map<String, WorkflowValidationResult> validate(DataObject dataToValidate) throws Exception {
//        //Récupère les champs à valider
//        Map<String,String> fieldsToValidate = dataToValidate.getFields();
//
//
//        Map<String, WorkflowValidationResult> results = new HashMap<>();
//
//        //complete la map fieldsToValidate s'il y a des champs manquant avec null comme value
//        //parmi tous les champs existant (les champs de chaque workflow)
//        for(RuleContainer ruleContainer : this.ruleContainerList){
//            completeMissingField(fieldsToValidate,ruleContainer.getRuleList());
//        }
//
//        //identifié le workflow
//        List<RuleContainer> ruleContainers = this.findRules(fieldsToValidate);
//
//        //si aucun workflow n'est identifié, retourne une map vide
//        if(ruleContainers == null || ruleContainers.isEmpty()) {
//            return results;
//            //throw new Exception("Aucun workflow identifié.");
//        }
//
//        //si des workflow sont identifiés
//        for(RuleContainer ruleContainer: ruleContainers) {
//            List<Rule> rules = ruleContainer.getRuleList();
//            Map<String, FieldValidationResult> workFlowResult = new HashMap<>();
//            rules.forEach(rule -> {
//                FieldValidationResult fieldResult = new FieldValidationResult();
//
//                //cherche la règle correspondant à la valeur du champ et ajouter à fieldsToValidate
//                // nouvelle key = "value" , value = valeur trouvée sinon null
//                this.addFieldValueWithRule(rule,fieldsToValidate);
//
//                boolean isValid = this.evaluateExpression(rule.getExpression(),fieldsToValidate);
//                fieldResult.setValid(isValid);
//                fieldResult.setMessage(isValid ? null : rule.getDescription());
//                workFlowResult.put( rule.getField().getLabel(), fieldResult);
//
//                fieldsToValidate.remove("value");
//            });
//            WorkflowValidationResult workflowValidationResult = new WorkflowValidationResult();
//            workflowValidationResult.setFieldsResult(workFlowResult);
//            results.put(ruleContainer.getWorkflow().getName(),workflowValidationResult);
//        }
//
//        return results;
//    }

    @Override
    public WorkflowValidationResult validate(DataObject dataToValidate) throws RuleValidationException {

        try { //Récupère les champs à valider
            Map<String, String> fieldsToValidate = dataToValidate.getFields();

            //complete la map fieldsToValidate s'il y a des champs manquant avec null comme value
            //parmi tous les champs existant (les champs de chaque workflow)
            for (RuleContainer ruleContainer : this.ruleContainerList) {
                completeMissingField(fieldsToValidate, ruleContainer.getRuleList());
            }
            //identifié le workflow
            List<RuleContainer> ruleContainers = this.findRules(fieldsToValidate);

            //si aucun workflow n'est identifié, retourne une map vide
            if (ruleContainers == null || ruleContainers.isEmpty()) {
                // return null;
                throw new RuleValidationException("Aucun workflow identifié pour les données fournies.");
            } else {
                //si des workflow sont identifiés
                WorkflowValidationResult result = new WorkflowValidationResult();

                for (RuleContainer ruleContainer : ruleContainers) {
                    List<Rule> rules = ruleContainer.getRuleList();
                    Map<String, FieldValidationResult> fieldsResult = new HashMap<>();
                    Map<String, FieldValidationResult> fieldsInvalidResult = new HashMap<>();
                    rules.forEach(rule -> {
                        FieldValidationResult fieldResult = new FieldValidationResult();

                        //cherche la règle correspondant à la valeur du champ et ajouter à fieldsToValidate
                        // nouvelle key = "value" , value = valeur trouvée sinon null
                        this.addFieldValueWithRule(rule, fieldsToValidate);

                        boolean isValid = this.evaluateExpression(rule.getExpression(), fieldsToValidate);
                        fieldResult.setValid(isValid);
                        fieldResult.setMessage(isValid ? null : rule.getDescription());
                        fieldsResult.put(rule.getField().getLabel(), fieldResult);

                        if (!isValid)
                            fieldsInvalidResult.put(rule.getField().getLabel(), fieldResult);

                        fieldsToValidate.remove("value");
                    });


                    result.addWorkflowName(ruleContainer.getWorkflow().getName());
                    result.setFieldsResult(this.union(result.getFieldsResult(),fieldsResult));
                    result.setFieldsInvalidResult(this.union(result.getFieldsInvalidResult(),fieldsInvalidResult));

                }
                return result;
            }
        }catch (RuleValidationException e) {
            // Gérer les erreurs spécifiques de validation
            logger.error("Erreur de validation : {}", e.getMessage(), e);
            throw e;
        }catch (NullPointerException e) {
            logger.error("Tentative d'accès à un objet nul : {}", e.getMessage());
            throw new RuleValidationException("Une erreur inattendue s'est produite.", e);
        } catch (ClassCastException e) {
            logger.error("Erreur de conversion de classe : {}", e.getMessage());
            throw new RuleValidationException("Erreur de type lors de l'évaluation de l'expression.", e);
        } catch (IllegalArgumentException e) {
            logger.error("Argument invalide : {}", e.getMessage());
            throw new RuleValidationException("Argument invalide fourni.", e);
        }catch (Exception e) {
            // Gérer toute autre exception non prévue
            logger.error("Une erreur inattendue est survenue pendant la validation : {}", e.getMessage(), e);
            throw new RuleValidationException("Une erreur inattendue est survenue pendant la validation : " + e.getMessage(), e);
        }
    }

    private Map<String,FieldValidationResult> union(Map<String,FieldValidationResult> r1, Map<String,FieldValidationResult> r2){
        if(r1 != null) {
            Map<String, FieldValidationResult> res = new HashMap<>(r1);
            for (Map.Entry<String, FieldValidationResult> entry : r2.entrySet()) {
                if (!res.containsKey(entry.getKey()))
                    res.put(entry.getKey(), entry.getValue());
                else {
                    boolean newValid = res.get(entry.getKey()).isValid() && entry.getValue().isValid();
                    String newMessage = res.get(entry.getKey()).getMessage();
                    if(newMessage == null ||  entry.getValue().getMessage() == null ||
                            ( res.get(entry.getKey()).getMessage() != null && entry.getValue().getMessage() != null &&
                                    res.get(entry.getKey()).getMessage().equals(entry.getValue().getMessage())))
                        newMessage = entry.getValue().getMessage();
                    else
                        newMessage = res.get(entry.getKey()).getMessage() + "  " + entry.getValue().getMessage();
                    FieldValidationResult newResult = new FieldValidationResult();
                    newResult.setMessage(newMessage);
                    newResult.setValid(newValid);
                    res.put(entry.getKey(), newResult);
                }
            }
            return res;
        }
        else
            return r2;
    }

    private void completeMissingField(Map<String,String> fieldsToValidate,List<Rule> rules){
        for(Rule rule : rules){
            fieldsToValidate.putIfAbsent(rule.getField().getLabel(),null);
        }
    }

    public List<RuleContainer> findRules(Map<String, String> fieldsToValidate) {
        return this.ruleContainerList.stream()
                .filter(ruleContainer -> this.evaluateExpression(ruleContainer.getWorkflow().getCondition(), fieldsToValidate))
                .distinct()
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

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

        Bindings bindings = engine.createBindings();
        bindings.putAll(fieldsToValidate);
        bindings.put("DateFormat", new DateFormat());
        bindings.put("DateBelongFormat", new DateBelongFormat());
        bindings.put("Major_Check", new Major_Check());
        bindings.put("Minor_Check", new Minor_Check());
        bindings.put("Length_Between", new Length_Between());
        bindings.put("Length_Less_Than", new Length_Less_Than());
        bindings.put("Length_Greater_Than", new Length_Greater_Than());
        bindings.put("LengthEqual", new LengthEqual());

        bindings.put("BornInFrance", new BornInFrance());
        bindings.put("BelongTo", new BelongTo());
        bindings.put("NotNull", new NotNull());
        bindings.put("IsNull", new IsNull());
        bindings.put("Equal", new Equal());

        bindings.put("ContainsOnlyCharacters", new ContainsOnlyCharacters());
        bindings.put("IsLowercase", new IsLowercase());
        bindings.put("IsUppercase", new IsUppercase());
        bindings.put("BeginUpperCase", new BeginUpperCase());

        bindings.put("IsNumber", new IsNumber());
        bindings.put("Number_Between", new Number_Between());
        bindings.put("Number_Greater_Than", new Number_Greater_Than());
        bindings.put("Number_Less_Than", new Number_Less_Than());
        bindings.put("NumberEqual", new NumberEqual());
        bindings.put("IsFloat", new IsFloat());
        bindings.put("Float_Between", new Float_Between());
        bindings.put("Float_Greater_Than", new Float_Greater_Than());
        bindings.put("Float_Less_Than", new Float_Less_Than());
        bindings.put("FloatEqual", new FloatEqual());

        bindings.put("valid", true);



//        System.out.println("value : "  + fieldsToValidate.get("value"));
//        System.out.println("condition : " + condition);

        try {
            Boolean r = (Boolean) engine.eval(condition, bindings);
            return r;
        } catch (ScriptException e) {
            /*System.err.println("Erreur lors de l'évaluation de l'expression : " + condition);
            e.printStackTrace();
            return false;*/
            logger.error("Erreur lors de l'évaluation de l'expression : {}", condition, e);
            return false;
        }catch (Exception e) {
            logger.error("Erreur imprévue lors de l'évaluation de l'expression : {}", condition, e);
            return false;
        }
    }

}
