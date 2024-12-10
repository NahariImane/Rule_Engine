package org.example.model;


public class Rule {
    private Field field;
    private String expression;
    private String description;

    public Rule(Field field,String expression,String description) {
        this.field = field;
        this.expression = expression;
        this.description = description;

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

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }

    @Override
    public String toString() {
        return "Rule[field=" + field + ", expression=" + expression + "]";
    }


}
