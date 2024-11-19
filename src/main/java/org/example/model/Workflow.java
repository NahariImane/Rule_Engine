package org.example.model;

import java.util.List;

public class Workflow {

    private String name;
    private List<Rule> rules;
    private String condition;

    public Workflow(String name ,String condition, List<Rule> rules ){
        this.name = name;
        this.condition= condition;
        this.rules = rules;
    }

    public String getName() {
        return name;
    }

    public String getCondition() {
        return condition;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public boolean validateField(String fieldName, Object value) {
        for (Rule rule : rules) {
            if (rule.getField().equals(fieldName)) {
                return rule.validate(value);
            }
        }
        System.out.println("Aucune règle trouvée pour le champ : " + fieldName);
        return false; // Aucun champ ne correspond
    }

    public boolean validateAllFields(DataObject object) {
        for (Rule rule : rules) {
            Object value = object.getField(rule.getField());
            if (!rule.validate(value)) {
                System.out.println("Validation échouée pour le champ : " + rule.getField());
                return false;
            }
        }
        return true; // Tous les champs sont valides
    }

    @Override
    public String toString() {
        return "Workflow[name=" + name + ", condition=" + condition + ", rules=" + rules + "]";
    }
}
