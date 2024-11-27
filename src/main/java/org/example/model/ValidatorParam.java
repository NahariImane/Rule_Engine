package org.example.model;

public class ValidatorParam {
    private final String ruleFile;
    private final ValidatorTypeEnum type;

    public ValidatorParam(String ruleFile, ValidatorTypeEnum type) {
        this.ruleFile = ruleFile;
        this.type = type;
    }

    public String getRuleFile() {
        return ruleFile;
    }

    public ValidatorTypeEnum getType() {
        return type;
    }
}
