package org.example;

import java.time.LocalDate;
import java.time.Period;

public class ValidationFunctions {
    // Fonction qui vérifie si une personne est mineure
    public static boolean Minor_Check(String birthDate, int ageLimit) {
        try {
            // Convertir la chaîne de caractères en date
            LocalDate birth = LocalDate.parse(birthDate);
            LocalDate today = LocalDate.now();
            // Calculer l'âge de la personne
            int age = Period.between(birth, today).getYears();
            return age < ageLimit; // Retourne vrai si l'âge est inférieur à la limite
        } catch (Exception e) {
            System.err.println("Erreur lors de l'évaluation de Minor_Check : " + e.getMessage());
            return false;
        }
    }
}
