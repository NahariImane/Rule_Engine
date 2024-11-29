package org.example;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class ValidationFunctions {

    public static class Minor_Check implements Function<String, Boolean> {
        @Override
        public Boolean apply(String birthDate) {
            try {
                // Convertir la chaîne de caractères en date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                // Convertir la chaîne en LocalDate
                LocalDate birth = LocalDate.parse(birthDate, formatter);
                LocalDate today = LocalDate.now();
                // Calculer l'âge de la personne
                int age = Period.between(birth, today).getYears();
                return age < 18; // Retourne vrai si l'âge est inférieur à 18
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de Minor_Check : " + e.getMessage());
                return false;
            }
        }
    }

    public static class Major_Check implements Function<String, Boolean> {
        @Override
        public Boolean apply(String birthDate) {
            try {
                // Convertir la chaîne de caractères en date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                // Convertir la chaîne en LocalDate
                LocalDate birth = LocalDate.parse(birthDate, formatter);
                LocalDate today = LocalDate.now();
                // Calculer l'âge de la personne
                int age = Period.between(birth, today).getYears();
                return age >= 18; // Retourne vrai si l'âge est supérieur à 18
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de Major_Check : " + e.getMessage());
                return false;
            }
        }
    }

    public static class IsValidDateFormat implements Function<String, Boolean> {
        @Override
        public Boolean apply(String date) {
            // Regex pour vérifier les formats
            String regex = "^(\\d{2}|__)/(\\d{2}|__)/\\d{4}$";

            // Vérifier si l'entrée correspond à la regex
            return date.matches(regex);
        }
    }

    // Custom functional interface Len_Check
    @FunctionalInterface
    public interface LengthCheck {
        boolean apply(String s, int min, int max);
    }

    // Len_Check implementation
    public static class Len_Check implements LengthCheck {
        @Override
        public boolean apply(String s, int min, int max) {
            try {
                // Validate if the name length is within the range
                return s.length() >= min && s.length() <= max;
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de Len_Check : " + e.getMessage());
                return false;
            }
        }
    }

    public static class  IsValidName implements Function<String,Boolean> {
        @Override
        public Boolean apply(String name) {
            try {
                //String allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz»«\"'()-/.,ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÑÒÓÔÕÖÙÚÛÜÝŸŒàáâãäåæçèéêëìíîïñòóôõöùúûüýÿœ";
                //String regex = "^[" + allowedCharacters + "]*$";
                String regex = "^[\\p{L}\\p{M}'\"()\\-/,À-ÖØ-öø-ÿŒœŸÝ]+$";

                // Vérifier si l'entrée correspond à la regex
                return name.matches(regex);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de IsValidName : " + e.getMessage());
                return false;
            }
        }
    }

    public static class  IsMinor implements Function<String,Boolean> {
        @Override
        public Boolean apply(String statut) {
            try {
                return statut.equals("minor");
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de IsMinor : " + e.getMessage());
                return false;
            }
        }
    }

    public static class  IsMajor implements Function<String,Boolean> {
        @Override
        public Boolean apply(String statut) {
            try {
                return statut.equals("major");
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de IsMajor : " + e.getMessage());
                return false;
            }
        }
    }

    public static class  BornInFrance implements Function<String,Boolean> {
        @Override
        public Boolean apply(String pays) {
            try {

                return "france".equals(pays);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de BornInFrance : " + e.getMessage());
                return false;
            }
        }
    }

    public static class IsValidTaille implements Function<String, Boolean> {
        @Override
        public Boolean apply(String value) {
            try {
                if (value == null) return false;

                // Vérifie si la valeur est un nombre flottant compris entre 0.5 et 3.0
                if (value.matches("^[0-9]+(\\.[0-9]+)?$")) {
                    float taille = Float.parseFloat(value);
                    return taille >= 0.5f && taille <= 3.0f;
                }
                return false;
            } catch (NumberFormatException e) {
                System.err.println("Erreur lors de l'évaluation de IsValidTaille : " + e.getMessage());
                return false;
            }
        }
    }

}
