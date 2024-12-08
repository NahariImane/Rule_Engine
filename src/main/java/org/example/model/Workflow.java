package org.example.model;

import java.util.List;

public class Workflow {
    private String name;

    // condition globale du workflow
    private String condition;

    public Workflow(String name, String condition) {
        this.name = name;
        this.condition = condition;
    }

    public String getName() {
        return name;
    }

    public String getCondition() {
        return condition;
    }
}

