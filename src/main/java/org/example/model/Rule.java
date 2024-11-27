package org.example.model;

import org.mvel2.MVEL;

import java.util.List;

public class Rule {
    private Field field;
    private String expression;

    public Rule() {
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "Rule[field=" + field + ", expression=" + expression + "]";
    }


}
