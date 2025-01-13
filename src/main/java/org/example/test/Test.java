package org.example.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.*;
import org.example.service.IValidator;
import org.example.service.ValidatorImpl;

import java.io.File;

public class Test {

    public static void main(String[] args) throws Exception {

        IValidator myValidator = new ValidatorImpl();
        ValidatorParam param = new ValidatorParam("src/main/Configuration/Rules_V2.xlsx", ValidatorTypeEnum.DATA);
        myValidator.start(param);

        ObjectMapper objectMapper = new ObjectMapper();
        DataObject dataObject = objectMapper.readValue(new File("src/main/java/org/example/test/dataTest.json"), DataObject.class);
        WorkflowValidationResult validationResponse = myValidator.validate(dataObject);


    }
}
