package org.example.model;

import org.example.service.RuleManager;

import java.util.Map;

public class ValidatorImpl  implements IValidator{

    private IRuleManager ruleManager;

    public ValidatorImpl() {
    }

    @Override
    public void start(ValidatorParam param) {
        this.ruleManager = RuleManager.getInstance();
        this.ruleManager.configure(param.getRuleFile());
    }

    @Override
    public Map<String, ValidationResult> validate(Map<String, String> fieldsToValidate) throws Exception {
        if(this.ruleManager == null) {
            throw new Exception("Validateur non démarré");
        }
        return this.ruleManager.validate(fieldsToValidate);
    }

}
