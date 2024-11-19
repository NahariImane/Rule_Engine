package org.example;

import org.example.model.DataObject;
import org.example.model.Workflow;
import org.example.service.RuleEngine;
import org.example.service.RuleValidator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.example.ValidationFunctions.Minor_Check;

public class Main {
    public static void main(String[] args) throws IOException {

       /* try {
            // Charger les workflows depuis le fichier Excel
            RuleEngine ruleEngine = new RuleEngine("src/main/Configuration/RulesTest.xlsx");
            RuleValidator ruleValidator = new RuleValidator();


            // Création des objets DataObject pour chaque test
            DataObject dataObject1 = new DataObject();
            dataObject1.setField("CHAMP_DEMANDE_TYPE", "demande");
            dataObject1.setField("CHAMP_Type_TITRE", "CNIE");
            dataObject1.setField("CHAMP_DATE_NAISSANCE", "2000-01-01");

            DataObject dataObject2 = new DataObject();
            dataObject2.setField("CHAMP_DEMANDE_TYPE", "demande");
            dataObject2.setField("CHAMP_Type_TITRE", "PSP");
            dataObject2.setField("CHAMP_DATE_NAISSANCE", "1990-01-01"); // Adulte
            dataObject2.setField("CHAMP_ADRESSE", "123 Main St");

            DataObject dataObject3 = new DataObject();
            dataObject3.setField("CHAMP_DEMANDE_TYPE", "demande");
            dataObject3.setField("CHAMP_Type_TITRE", "PSP");
            dataObject3.setField("CHAMP_DATE_NAISSANCE", "2010-01-01"); // Mineur
            dataObject3.setField("CHAMP_PRENOM", "Imane");



           validateDataObject(dataObject1,ruleEngine, ruleValidator);

        } catch (IOException e) {
            e.printStackTrace();
        }*/
        RuleEngine ruleEngine = new RuleEngine("src/main/Configuration/RulesTest.xlsx");

        DataObject object = new DataObject();
        //object.setField("DEMANDE_TYPE","DEMANDE");
        object.setField("CHAMP_DATE_NAISSANCE","2010-01-01");
        object.setField("CHAMP_PRENOM","Imane");
        object.setField("CHAMP_TYPE_TITRE","PSP");
        object.setField("CHAMP_ADRESSE","test adresse");

        Workflow workflow = ruleEngine.identifyWorkflow(object);
        if(workflow ==null)

        {
            System.out.println("Aucun workflow correspondant trouvé.");
        } else

        {
            System.out.println("Workflow identifié : " + workflow.getName());
           // boolean isValid = ruleEngine.validateWorkflow(object, workflow);
            boolean isValid = workflow.validateAllFields(object);
            //System.out.println("Le workflow est-il valide ? " + isValid);

            System.out.println("Validation globale du workflow : " + (isValid ? "Valide" : "Invalide"));
        }



    }







}