package org.example.model;

public class Field {

    private String label;
    private String type;
    private boolean isObligatory;

    public Field(String label, String type,boolean isObligatory) {
        this.label = label;
        this.type = type;
        this.isObligatory = isObligatory;
    }

    public String getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }

    public boolean isObligatory() {
        return isObligatory;
    }
}
