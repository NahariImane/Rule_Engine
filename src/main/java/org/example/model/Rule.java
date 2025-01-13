package org.example.model;


public class Rule {
    private final Field field;
    private final String expression;
    private final String description;

    public Rule(Field field, String expression, String description) {
        this.field = field;
        this.expression = expression;
        this.description = description;
    }

    public Field getField() {
        return field;
    }

    public String getExpression() {
        return expression;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return "Rule[field=" + field + ", expression=" + expression + "]";
    }


}
