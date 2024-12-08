package org.example.controller;


import org.example.model.*;
import org.example.service.ValidatorImpl;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;


@RestController
public class API {
    IValidator myValidator;

    @RequestMapping("/")
    public String home(){
        return "Hello World!";
    }

    @PostMapping("/start")
    public String start()  {
        try{
            this.myValidator = new ValidatorImpl();
            ValidatorParam param = new ValidatorParam("src/main/Configuration/RulesTest.xlsx", ValidatorTypeEnum.DATA);
            myValidator.start(param);
            return "Start successful";
        }catch (IOException e) {
            throw new RuntimeException(e);
//            return "Start fail";
        }
    }

    @PostMapping("/validate")
    public Map<String, WorkflowValidationResult> validate(@RequestBody DataObject inputJson) {
        try {
            return myValidator.validate(inputJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
