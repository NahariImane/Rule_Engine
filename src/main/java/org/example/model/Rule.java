package org.example.model;

import org.mvel2.MVEL;

public class Rule {
    private String field;
    private String expression;

    public Rule(String field, String expression ){
        this.field = field;
        this.expression = expression;
    }

    public String getField() {
        return field;
    }
    public String getExpression() {
        return expression;
    }


    public boolean validate(Object value) {
        // Remplacer %value% dans la règle par la valeur réelle
        String evaluatedExpression = expression.replace("%value%",
                value == null ? "null" : "'" + value.toString() + "'");
        try {
            return (Boolean) MVEL.eval(evaluatedExpression); // Évaluer l'expression
        } catch (Exception e) {
            System.err.println("Erreur de validation de la règle : " + evaluatedExpression);
            return false;
        }
    }
    @Override
    public String toString() {
        return "Rule[field=" + field + ", expression=" + expression + "]";
    }
}
