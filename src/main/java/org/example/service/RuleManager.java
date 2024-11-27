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
import java.util.*;
import java.util.stream.Collectors;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.function.Function;
import java.time.LocalDate;

public class RuleManager implements IRuleManager {
    private List<RuleContainer> ruleContainerList; // Liste de tous les containers chargés
    private String ruleFile;

    private static final RuleManager INSTANCE = new RuleManager();

    public static RuleManager getInstance() {
        return INSTANCE;
    }

    private void loadRules() {

        if(ruleContainerList == null) {
            ruleContainerList = new ArrayList<>();
        }

        //TODO: load excel file

        //RENOUVELLEMENT PSP SIMPLIFIE MINEUR
        Workflow workflowMineur = new Workflow("PSP_RENOUVELLEMENT_SIMPLIFIE_MINEUR",
                "TYPE_TITRE == 'PSP' && MINEUR_MAJEUR == 'MINEUR'");
        List<Rule> ruleList = new ArrayList<>();
        Rule ruleNom = new Rule();
        ruleNom.setField(new Field("NOM", "TEXTE"));
        ruleNom.setExpression("NOM != '' && NOM.length <= 22");

        Rule ruleDateNaissance = new Rule();
        ruleDateNaissance.setField(new Field("DATE_NAISSANCE", "DATE"));
        ruleDateNaissance.setExpression("DATE_NAISSANCE != '' && DATE_FORMAT(DATE_NAISSANCE) == 'DD-MM-YYYY' && MINEUR(DATE_NAISSANCE)");

        ruleList.add(ruleNom);
        ruleList.add(ruleDateNaissance);

        RuleContainer pspRSMineur = new RuleContainer(workflowMineur, ruleList);


        //RENOUVELLEMENT PSP SIMPLIFIE MAJEUR
        Workflow workflowMajeur = new Workflow("PSP_RENOUVELLEMENT_SIMPLIFIE_MAJEUR", "TYPE_TITRE == 'PSP' && MINEUR_MAJEUR == 'MAJEUR'");
        List<Rule> ruleListMaj = new ArrayList<>();
        Rule ruleNomMaj = new Rule();
        ruleNomMaj.setField(new Field("NOM", "TEXTE"));
        ruleNomMaj.setExpression("NOM != '' && NOM.length <= 22");

        Rule ruleDateNaissanceMaj = new Rule();
        ruleDateNaissanceMaj.setField(new Field("DATE_NAISSANCE", "DATE"));
        ruleDateNaissanceMaj.setExpression("DATE_NAISSANCE != '' && DATE_FORMAT(DATE_NAISSANCE) == 'DD-MM-YYYY' && MAJEUR(DATE_NAISSANCE)");

        ruleListMaj.add(ruleNomMaj);
        ruleListMaj.add(ruleDateNaissanceMaj);

        RuleContainer pspRSMajeur = new RuleContainer(workflowMajeur, ruleListMaj);

        ruleContainerList.add(pspRSMineur);
        ruleContainerList.add(pspRSMajeur);

    }

    public List<RuleContainer> findRules(Map<String, String> fieldsToValidate) {
        return this.ruleContainerList.stream()
                .filter(ruleContainer -> this.evaludateExpression(ruleContainer.getWorkflow().getCondition(), fieldsToValidate))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, ValidationResult> validate(Map<String, String> fieldsToValidate) throws Exception {

        Map<String, ValidationResult> results = new HashMap<>();

        List<RuleContainer> ruleContainers = this.findRules(fieldsToValidate);

        if(ruleContainers == null || ruleContainers.isEmpty()) {
            throw new Exception("Règles non chargées.");
        }

        for(RuleContainer ruleContainer: ruleContainers) {
            System.out.println("Workflow :" + ruleContainer.getWorkflow().getName());
            List<Rule> rules = ruleContainer.getRuleList();

            rules.forEach(rule -> {
                ValidationResult result = new ValidationResult();
                boolean isValid = this.evaludateExpression(rule.getExpression(), fieldsToValidate);
                result.setValid(isValid);
                result.setMessage(isValid ? null : "Non valid");
                results.put(rule.getField().getLabel(), result);
            });

        }

        return results;
    }

    @Override
    public void configure(String ruleFilePath) {
        this.ruleFile = ruleFilePath;
        this.loadRules();
    }

    public boolean evaludateExpression(String condition, Map<String, String> fieldsToValidate) {
        //TODO: evaluate expression
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

        Bindings bindings = engine.createBindings();
        bindings.putAll(fieldsToValidate);
        bindings.put("DATE_FORMAT", new DateFormatGetter());
        bindings.put("MINEUR", new MinorVerifier());
        bindings.put("MAJEUR", new MajorVerifier());

        try {
            Boolean r = (Boolean) engine.eval(condition, bindings);
            return r;
        } catch (ScriptException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class DateFormatGetter implements Function<String, String> {

        @Override
        public String apply(String s) {
            //TODO: verify date format
            return "DD-MM-YYYY";
        }
    }

    private static class MinorVerifier implements Function<String, Boolean> {

        @Override
        public Boolean apply(String s) {
            LocalDate date = LocalDate.parse(s);
            int age = LocalDate.now().compareTo(date);
            System.out.println("age =" + age);
            return age < 18;
        }
    }

    private static class MajorVerifier implements Function<String, Boolean> {

        @Override
        public Boolean apply(String s) {
            LocalDate date = LocalDate.parse(s);
            int age = LocalDate.now().compareTo(date);
            System.out.println("age =" + age);
            return age >= 18;
        }
    }
   /* public RuleManager() {
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
    }*/


}
