package org.example.service;
import org.example.exception.RuleLoadingException;
import org.junit.jupiter.api.Test;

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




}
