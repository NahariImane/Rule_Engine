package org.example.controller;


import org.example.exception.RuleLoadingException;
import org.example.exception.RuleValidationException;
import org.example.model.*;
import org.example.service.ValidatorImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> testStart() {
        try {
            this.myValidator = new ValidatorImpl();
//            ValidatorParam param = new ValidatorParam("src/main/Configuration/Rules_V1.xlsx", ValidatorTypeEnum.DATA);
            ValidatorParam param = new ValidatorParam("src/main/Configuration/Rules_V2.xlsx", ValidatorTypeEnum.DATA);
            myValidator.start(param);
            return ResponseEntity.ok("Start successful");
        } catch (RuleLoadingException e) {
            // Retourner une réponse HTTP 400 avec le message de l'exception
            return ResponseEntity.badRequest().body("Erreur de chargement des règles : " + e.getMessage());
        } catch (IOException e) {
            // Retourner une réponse HTTP 500 avec le message de l'exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur système lors de l'initialisation : " + e.getMessage());
        }
    }

//    @PostMapping("/validate")
//    public Map<String, WorkflowValidationResult> testValidate(@RequestBody DataObject inputJson) {
//        try {
//            return myValidator.validate(inputJson);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    /*@PostMapping("/validate")
    public WorkflowValidationResult testValidate(@RequestBody DataObject inputJson) {
        try {
            return myValidator.validate(inputJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/
    @PostMapping("/validate")
    public ResponseEntity<?> testValidate(@RequestBody DataObject inputJson) {
        try {
            WorkflowValidationResult result = myValidator.validate(inputJson);

            // Retourner le résultat en cas de succès
            return ResponseEntity.ok(result);

        } catch (RuleValidationException e) {
            // Exception spécifique liée aux règles de validation
            return ResponseEntity
                    .badRequest()
                    .body("Erreur de validation des règles : " + e.getMessage());

        } catch (Exception e) {
            // Exception inattendue
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne : Une erreur inattendue est survenue. " + e.getMessage());
        }
    }


}
