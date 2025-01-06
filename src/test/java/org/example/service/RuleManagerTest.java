package org.example.service;
import org.example.exception.RuleLoadingException;
import org.example.exception.RuleValidationException;
import org.example.model.*;
import org.junit.jupiter.api.Test;
import org.example.service.RuleManager;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


public class RuleManagerTest {

    @Test
    void testGetInstance() {
        RuleManager instance1 = RuleManager.getInstance();
        RuleManager instance2 = RuleManager.getInstance();
        assertNotNull(instance1, "L'instance retournée ne doit pas être null.");
        assertNotNull(instance2, "L'instance retournée ne doit pas être null.");
        assertSame(instance1, instance2, "Les deux instances doivent être identiques.");
    }


    @Test
    void testConfigureWithValidFilePath() throws IOException, RuleLoadingException {
        // Chemin du fichier valide
        String validFilePath = "src/test/Configuration/Rules_V2.xlsx";

        // Créez un spy de l'instance singleton de RuleManager
        RuleManager ruleManager = spy(RuleManager.getInstance());

        // Simuler la méthode validateExcelStructure et loadRules pour qu'elles ne fassent rien
        doNothing().when(ruleManager).validateExcelStructure();
        doNothing().when(ruleManager).loadRules();

        // Appel de la méthode configure avec un chemin de fichier valide
        ruleManager.configure(validFilePath);

        verify(ruleManager).configure(validFilePath);
    }


    /*@Test
    void testValidateWithValidData() throws RuleValidationException {
        // Mock de DataObject avec des champs valides
        DataObject dataToValidate = mock(DataObject.class);
        RuleManager ruleManager = spy(RuleManager.getInstance());
        Map<String, String> fieldsToValidate = new HashMap<>();
        fieldsToValidate.put("field1", "value1");
        fieldsToValidate.put("field2", "value2");
        when(dataToValidate.getFields()).thenReturn(fieldsToValidate);

        // Création des objets Field
        Field field1 = new Field("field1");
        Field field2 = new Field("field2");

        // Création de règles avec les champs
        Rule rule1 = new Rule(field1, "value1 != null", "field1 is invalid");
        Rule rule2 = new Rule(field2, "value2 != null", "field2 is invalid");

        // Mock de Workflow
        Workflow workflow = new Workflow("TestWorkflow", "condition");

        // Création de RuleContainer avec les règles
        RuleContainer ruleContainer = new RuleContainer(workflow, Arrays.asList(rule1, rule2));

        // Assurez-vous que findRules retourne bien un RuleContainer avec des règles
        doReturn(Collections.singletonList(ruleContainer)).when(ruleManager).findRules(fieldsToValidate);

        // Simuler l'évaluation des expressions
        doReturn(true).when(ruleManager).evaluateExpression(anyString(), eq(fieldsToValidate));

        // Appel de la méthode validate
        WorkflowValidationResult result = ruleManager.validate(dataToValidate);

        // Assertions
        assertNotNull(result, "Le résultat ne doit pas être null.");
        assertTrue(result.getWorkflowNames().contains("TestWorkflow"), "Le workflow 'TestWorkflow' devrait être identifié.");
        assertEquals(2, result.getFieldsResult().size(), "Deux champs doivent être validés.");
        assertTrue(result.getFieldsResult().get("field1").isValid(), "field1 devrait être valide.");
        assertTrue(result.getFieldsResult().get("field2").isValid(), "field2 devrait être valide.");
    }*/






}
