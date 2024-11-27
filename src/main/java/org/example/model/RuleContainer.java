package org.example.model;

import java.util.List;

public class RuleContainer {
    private final Workflow workflow;
    private final List<Rule> ruleList;


    public RuleContainer(Workflow workflow, List<Rule> ruleList) {
        this.workflow = workflow;
        this.ruleList = ruleList;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public List<Rule> getRuleList() {
        return ruleList;
    }
}
