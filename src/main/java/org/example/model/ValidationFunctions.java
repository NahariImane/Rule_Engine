package org.example.model;

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

    public static class Valid_Date implements Function<String, Boolean> {
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
    public interface ILength_Between {
        boolean apply(String s, int min, int max);
    }

    // Len_Check implementation
    public static class Length_Between implements ILength_Between {
        @Override
        public boolean apply(String s, int min, int max) {
            try {
                // Validate if the name length is within the range
                return s.length() >= min && s.length() <= max;
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de Length_Between : " + e.getMessage());
                return false;
            }
        }
    }

    // Custom functional interface Len_Check
    @FunctionalInterface
    public interface ILength_Greater {
        boolean apply(String s, int max);
    }

    public static class Length_Greater_Than implements ILength_Greater {
        @Override
        public boolean apply(String s, int n) {
            try {
                return s.length() > n;
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de Length_Greater : " + e.getMessage());
                return false;
            }
        }
    }

    // Custom functional interface Len_Check
    @FunctionalInterface
    public interface ILength_Less_Than {
        boolean apply(String s, int n);
    }

    public static class Length_Less_Than implements ILength_Less_Than {
        @Override
        public boolean apply(String s, int n) {
            try {
                return s.length() < n;
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de Length_Greater : " + e.getMessage());
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


    public static class  BornInFrance implements Function<String,Boolean> {
        @Override
        public Boolean apply(String pays) {
            try {
                String[] stringList = {"france","Frane","FRANCE"};
                for(String s : stringList){
                    if(s.equals(pays)) return true;
                }
                return false;
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de BornInFrance : " + e.getMessage());
                return false;
            }
        }
    }

    public static class  IsNumber implements Function<String,Boolean> {
        @Override
        public Boolean apply(String department) {
            try {
                Integer.parseInt(department);
                return true;
            } catch (Exception e) {
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


    @FunctionalInterface
    public interface IBelongTo {
        Boolean apply(String value, String list);
    }
    public static class  BelongTo implements IBelongTo {
        @Override
        public Boolean apply(String value, String list) {
            try {
                String[] l = list.split(",\\s*");
                for(String s : l){
                    if(value.equals(s)) return true;
                }
                return false;
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de BelongTo : " + e.getMessage());
                return false;
            }
        }
    }

    public static class NotNull implements Function<String, Boolean> {
        @Override
        public Boolean apply(String value) {
            try {
                return value!=null;
            } catch (NumberFormatException e) {
                System.err.println("Erreur lors de l'évaluation de NotNull : " + e.getMessage());
                return false;
            }
        }
    }

    public static class IsNull implements Function<String, Boolean> {
        @Override
        public Boolean apply(String value) {
            try {
                return value==null;
            } catch (NumberFormatException e) {
                System.err.println("Erreur lors de l'évaluation de IsNull : " + e.getMessage());
                return false;
            }
        }
    }

}
