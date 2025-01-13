package org.example.controller;


import org.example.exception.DataException;
import org.example.exception.RuleLoadingException;
import org.example.exception.RuleValidationException;
import org.example.model.*;
import org.example.service.IValidator;
import org.example.service.ValidatorImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
public class API {
    IValidator myValidator;

    @RequestMapping("/")
    public String home() {
        return "Test du moteur de validation !";
    }

    @PostMapping("/rechargerFichierExcel")
    public ResponseEntity<?> testStart() {
        try {
            this.myValidator = new ValidatorImpl();
            String filePath = "src/main/Configuration/Rules_V2.xlsx";
            ValidatorParam param = new ValidatorParam(filePath, ValidatorTypeEnum.DATA);

            myValidator.start(param);

            // Si tout est valide et fonctionne correctement
            ValidationResponse ExcelFileResponse = new ValidationResponse();
            ExcelFileResponse.setCodeStatus(CodeStatus.SUCCESS.name());
            ExcelFileResponse.setMessage("Fichier Chargé avec success");
            return ResponseEntity.ok(ExcelFileResponse);

            //  return ResponseEntity.ok("Start successful");
        } catch (RuleLoadingException e) {
            // Retourner une réponse HTTP 400 avec le message de l'exception
            ValidationResponse response = new ValidationResponse();
            response.setCodeStatus(CodeStatus.FORMAT_EXCEL_INVALID.name());
            response.setMessage("Erreur dans le fichier Excel : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (IOException e) {
            // Retourner une réponse HTTP 500 avec le message de l'exception
            ValidationResponse response = new ValidationResponse();
            response.setCodeStatus(CodeStatus.FORMAT_EXCEL_INVALID.name());
            response.setMessage("Erreur système lors de l'initialisation : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> testValidate(@RequestBody DataObject inputJson) {
        try {
            WorkflowValidationResult result = myValidator.validate(inputJson);

            ValidationResponse response = new ValidationResponse();

            // Si la validation est réussie
            if (result != null && result.isValid()) {
                response.setCodeStatus(CodeStatus.SUCCESS.name());
                response.setMessage("Validation réussie");
                response.setData(result);
            } else {
                response.setCodeStatus(CodeStatus.VALIDATION_FAILED.name()); // En cas d'échec
                response.setMessage("La validation a échoué. Voir les détails ci-dessous.");
                response.setData(result);
            }

            // Retourner le résultat en cas de succès
            return ResponseEntity.ok(response);

        } catch (RuleValidationException e) {
            // En cas d'exception liée aux règles de validation
            ValidationResponse response = new ValidationResponse();
            response.setCodeStatus(CodeStatus.VALIDATION_FAILED.name());
            response.setMessage("Erreur de validation des règles : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (DataException e) {
            ValidationResponse response = new ValidationResponse();
            response.setCodeStatus(CodeStatus.FORMAT_DONNEE_INVALID.name()); // Exemple d'un autre statut d'erreur
            response.setMessage("Erreur sur les données d'entrée. " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            // En cas d'exception générale
            ValidationResponse response = new ValidationResponse();
//            response.setCodeStatus(CodeStatus.FORMAT_DONNEE_INVALID.name()); // Exemple d'un autre statut d'erreur
            response.setMessage("Erreur interne : Une erreur inattendue est survenue. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }


}
