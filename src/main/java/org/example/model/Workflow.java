package org.example.model;

import java.util.List;

public class Workflow {

    private String name;
    private List<Rule> rules;

    public Workflow(String name , List<Rule> rules ){
        this.name = name;
        this.rules = rules;
    }

    public String getName() {
        return name;
    }

    public List<Rule> getRules() {
        return rules;
    }
}
