package org.example.model;

import java.util.List;

public class RuleContainer {
    private Workflow workflow;
    private List<Field> fields; // Liste des champs liés a ce workflow
    private List<Rule> rules;   // Liste des règles liées a ces champs du  workflow

    public RuleContainer(Workflow workflow, List<Field> fields, List<Rule> rules) {
        this.workflow = workflow;
        this.fields = fields;
        this.rules = rules;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public List<Field> getFields() {
        return fields;
    }

    public List<Rule> getRules() {
        return rules;
    }

    /*
    public void addField(Field field) {
        this.fields.add(field);
    }

    public void addRule(Rule rule) {
        this.rules.add(rule);
    }*/
}
