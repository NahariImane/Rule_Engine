package org.example.model;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ValidationFunctions {
    // Liste des formats possibles
    private static final List<DateTimeFormatter> DATE_FORMATTERS = new ArrayList<>();

    static {

        DATE_FORMATTERS.add(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        DATE_FORMATTERS.add(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        DATE_FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        DATE_FORMATTERS.add(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        DATE_FORMATTERS.add(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    public static class Minor_Check implements Function<String, Boolean> {
        @Override
        public Boolean apply(String value) {
            if (value == null || value.isEmpty()) {
                return false; // Retourne false si la date est vide ou nulle
            }

            for (DateTimeFormatter formatter : DATE_FORMATTERS) {
                try {
                    // Convertir la chaîne en LocalDate avec un des formats disponibles
                    LocalDate birth = LocalDate.parse(value, formatter);
                    LocalDate today = LocalDate.now();

                    // Calculer l'âge
                    int age = Period.between(birth, today).getYears();

                    // Retourne vrai si l'âge est inférieur à 18
                    return age < 18;
                } catch (DateTimeParseException e) {
                    // Ignorer et essayer le format suivant
                }
            }

            // Si aucun format ne fonctionne, afficher une erreur et retourner faux
            System.err.println("Erreur : Le format de la valeur n'est pas valide : " + value);
            return false;
        }
    }

    public static class Major_Check implements Function<String, Boolean> {
        @Override
        public Boolean apply(String value) {
            if (value == null || value.isEmpty()) {
                return false; // Retourne false si la date est vide ou nulle
            }

            for (DateTimeFormatter formatter : DATE_FORMATTERS) {
                try {
                    // Convertir la chaîne en LocalDate avec un des formats disponibles
                    LocalDate birth = LocalDate.parse(value, formatter);
                    LocalDate today = LocalDate.now();

                    // Calculer l'âge
                    int age = Period.between(birth, today).getYears();

                    // Retourne vrai si l'âge est supérieur ou égal à 18
                    return age >= 18;
                } catch (DateTimeParseException e) {
                    // Ignorer et essayer le format suivant
                }
            }

            // Si aucun format ne fonctionne, afficher une erreur et retourner faux
            System.err.println("Erreur : Le format de la valeur n'est pas valide : " + value);
            return false;
        }
    }

    @FunctionalInterface
    public interface IDateFormat {
        boolean apply(String date,String format);
    }
    public static class DateFormat implements IDateFormat {
        @Override
        public boolean apply(String value, String format) {
            if (value == null || value.isEmpty() || format == null || format.isEmpty()) {
                return false; // Retourne false si la chaîne ou le format est vide ou nul
            }

            try {
                // Créer un DateTimeFormatter basé sur le format donné
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

                // Tenter de parser la date avec le format
                formatter.parse(value);

                // Si aucune exception n'est levée, la date est valide
                return true;
            } catch (DateTimeParseException e) {
                // Retourne false si la date ne correspond pas au format
                return false;
            }
        }
    }

    @FunctionalInterface
    public interface IDateBelongFormat {
        boolean apply(String date,String listFormat);
    }
    public static class DateBelongFormat implements IDateBelongFormat {
        @Override
        public boolean apply(String value, String listFormat) {

                if (value == null || value.isEmpty() || listFormat == null || listFormat.isEmpty()) {
                    return false; // La date ou le format est invalide
                }
                String[] formats = listFormat.split(",\\s*");

                for(String format : formats) {
                    try {
                        // Créer un DateTimeFormatter basé sur le format donné
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

                        // Tenter de parser la date avec le format
                        formatter.parse(value);

                        // Si aucun exception n'est levée, la date est valide
                        return true;
                    }
                    catch (DateTimeParseException e) {
                        //On ignore
                    }
                }
                return false;
        }
    }

    // Custom functional interface Len_Check
    @FunctionalInterface
    public interface ILength_Between {
        boolean apply(String value, int min, int max);
    }

    // Len_Check implementation
    public static class Length_Between implements ILength_Between {
        @Override
        public boolean apply(String value, int min, int max) {
            // Validation des paramètres min et max
            if (min > max) {
                throw new IllegalArgumentException("Le minimum doit être inférieur ou égal au maximum.");
            }

            // Validation de la chaîne de caractères
            if (value == null || value.isEmpty()) {
                return false; // Retourne false si la chaîne est nulle ou vide
            }

            // Vérifier si la longueur est dans l'intervalle [min, max]
            int length = value.length();
            return length >= min && length <= max;
        }
    }

    // Custom functional interface Len_Check
    @FunctionalInterface
    public interface ILength_Greater {
        boolean apply(String value, int min);
    }

    public static class Length_Greater_Than implements ILength_Greater {
        @Override
        public boolean apply(String value, int min) {
            try {
                if(value != null && !value.isEmpty()) {
                    return value.length() > min;
                }
                return false;
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de Length_Greater : " + e.getMessage());
                return false;
            }
        }
    }

    // Custom functional interface Len_Check
    @FunctionalInterface
    public interface ILength_Less_Than {
        boolean apply(String value, int max);
    }

    public static class Length_Less_Than implements ILength_Less_Than {
        @Override
        public boolean apply(String value, int max) {
            try {
                if(value != null && !value.isEmpty()) {
                    return value.length() < max;
                }
                return false;
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de Length_Greater : " + e.getMessage());
                return false;
            }
        }
    }

    @FunctionalInterface
    public interface ILengthEqual {
        Boolean apply(String value, int n);
    }
    public static class  LengthEqual implements ILengthEqual {
        @Override
        public Boolean apply(String value, int n) {
            try {
                if(value != null && !value.isEmpty())
                    return value.length() == n;
                return false;
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de LengthEqual : " + e.getMessage());
                return false;
            }
        }
    }

//    public static class  IsValidName implements Function<String,Boolean> {
//        @Override
//        public Boolean apply(String name) {
//            try {
//                if(name != null && !name.isEmpty()) {
////                    String allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz»«\"'()-/.,ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÑÒÓÔÕÖÙÚÛÜÝŸŒàáâãäåæçèéêëìíîïñòóôõöùúûüýÿœ";
////                    String regex = "^[" + Pattern.quote(allowedCharacters) + "]*$";
//                    String regex = "^[\\p{L}\\p{M}'\"()\\-/,À-ÖØ-öø-ÿŒœŸÝ]+$";
//
//                    // Vérifier si l'entrée correspond à la regex
//                    return name.matches(regex);
//                }
//                return false;
//            } catch (Exception e) {
//                System.err.println("Erreur lors de l'évaluation de IsValidName : " + e.getMessage());
//                return false;
//            }
//        }
//    }

    @FunctionalInterface
    public interface IContainsOnlyCharacters {
        boolean apply(String value, String characters);
    }

    public static class ContainsOnlyCharacters implements IContainsOnlyCharacters {
        @Override
        public boolean apply(String value, String characters) {
            try {
                if(value != null && !value.isEmpty() &&  characters != null && !characters.isEmpty()) {
                    String regex = "^[" + Pattern.quote(characters) + "]*$";
                    return value.matches(regex);
                }
                return false;
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de ContainsOnlyCharacters : " + e.getMessage());
                return false;
            }
        }
    }


    public static class  BornInFrance implements Function<String,Boolean> {
        @Override
        public Boolean apply(String value) {
            try {
                if(value != null && !value.isEmpty()) {
                    String[] stringList = {"france", "Frane", "FRANCE"};
                    for (String s : stringList) {
                        if (s.equals(value)) return true;
                    }
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
        public Boolean apply(String value) {
            if (value == null || value.isEmpty()) {
                return false; // Retourne false si la chaîne est nulle ou vide
            }

            try {
                // Vérifie si la valeur est un entier
                Integer.parseInt(value);
                return true;
            } catch (NumberFormatException e) {
                return false; // Retourne false si ce n'est pas un entier valide
            }

        }
    }

    @FunctionalInterface
    public interface INumber_Between{
        boolean apply(String value, int min, int max);
    }
    public static class Number_Between implements INumber_Between {
        @Override
        public boolean apply(String value, int min, int max) {
            // Vérification initiale des paramètres
            if (value == null || value.isEmpty()) {
                return false; // Retourne false si la chaîne est nulle ou vide
            }

            if (min >= max) {
                throw new IllegalArgumentException("Le minimum doit être inférieur au maximum.");
            }

            try {
                // Conversion de la chaîne en int
                int v = Integer.parseInt(value);
                // Validation si la valeur est dans la plage
                return v >= min && v <= max;
            } catch (NumberFormatException e) {
                // Retourne false si la conversion échoue
                return false;
            }
        }
    }

    // Custom functional interface Len_Check
    @FunctionalInterface
    public interface INumber_Greater_Than {
        boolean apply(String value, int min);
    }

    public static class Number_Greater_Than implements INumber_Greater_Than {
        @Override
        public boolean apply(String value, int min) {
            // Vérifier si la chaîne est nulle ou vide
            if (value == null || value.isEmpty()) {
                return false;
            }

            try {
                // Convertir la chaîne en int
                float v = Integer.parseInt(value);
                // Vérifier si la valeur est strictement supérieure au maximum
                return v > min;
            } catch (NumberFormatException e) {
                // Retourner false si la chaîne n'est pas un float valide
                return false;
            }
        }
    }

    // Custom functional interface Len_Check
    @FunctionalInterface
    public interface INumber_Less_Than {
        boolean apply(String value, int max);
    }

    public static class Number_Less_Than implements INumber_Less_Than {
        @Override
        public boolean apply(String value, int max) {
            // Vérification initiale des paramètres
            if (value == null || value.isEmpty()) {
                return false; // Retourne false si la chaîne est nulle ou vide
            }

            try {
                // Convertir la chaîne en int
                float v = Integer.parseInt(value);
                // Vérifier si la valeur est strictement inférieure au minimum
                return v < max;
            } catch (NumberFormatException e) {
                // Retourne false si la conversion échoue
                return false;
            }
        }
    }

    @FunctionalInterface
    public interface INumberEqual {
        boolean apply(String value, int n);
    }
    public static class  NumberEqual implements INumberEqual {
        @Override
        public boolean apply(String value, int n) {
            if (value == null || value.isEmpty()) {
                return false; // Retourne false si la chaîne est nulle ou vide
            }

            try {
                // Vérifie si la valeur est un entier
                int v = Integer.parseInt(value);
                return v == n;
            } catch (NumberFormatException e) {
                return false; // Retourne false si ce n'est pas un entier valide
            }

        }
    }

    public static class  IsFloat implements Function<String,Boolean> {
        @Override
        public Boolean apply(String value) {
            try {
                if(value != null && !value.isEmpty()) {
                    Float.parseFloat(value);
                    return true;
                }
                return false;
            } catch (NumberFormatException e) {
                return false; // Retourne false si ce n'est pas un entier valide
            }catch (Exception e) {
                return false;
            }
        }
    }

    @FunctionalInterface
    public interface IFloat_Between{
        boolean apply(String value, float min, float max);
    }
    public static class Float_Between implements IFloat_Between {
        @Override
        public boolean apply(String value, float min, float max) {
            // Vérification initiale des paramètres
            if (value == null || value.isEmpty()) {
                return false; // Retourne false si la chaîne est nulle ou vide
            }

            if (min >= max) {
                throw new IllegalArgumentException("Le minimum doit être inférieur au maximum.");
            }

            try {
                // Conversion de la chaîne en float
                float v = Float.parseFloat(value);
                // Validation si la valeur est dans la plage
                return v >= min && v <= max;
            } catch (NumberFormatException e) {
                // Retourne false si la conversion échoue
                return false;
            }
        }
    }

    // Custom functional interface Len_Check
    @FunctionalInterface
    public interface IFloat_Greater_Than {
        boolean apply(String value, float min);
    }

    public static class Float_Greater_Than implements IFloat_Greater_Than {
        @Override
        public boolean apply(String value, float min) {
            // Vérifier si la chaîne est nulle ou vide
            if (value == null || value.isEmpty()) {
                return false;
            }

            try {
                // Convertir la chaîne en Float
                float v = Float.parseFloat(value);
                // Vérifier si la valeur est strictement supérieure au maximum
                return v > min;
            } catch (NumberFormatException e) {
                // Retourner false si la chaîne n'est pas un float valide
                return false;
            }
        }
    }

    // Custom functional interface Len_Check
    @FunctionalInterface
    public interface IFloat_Less_Than {
        boolean apply(String value, float max);
    }

    public static class Float_Less_Than implements IFloat_Less_Than {
        @Override
        public boolean apply(String value, float max) {
            // Vérification initiale des paramètres
            if (value == null || value.isEmpty()) {
                return false; // Retourne false si la chaîne est nulle ou vide
            }

            try {
                // Convertir la chaîne en Float
                float v = Float.parseFloat(value);
                // Vérifier si la valeur est strictement inférieure au minimum
                return v < max;
            } catch (NumberFormatException e) {
                // Retourne false si la conversion échoue
                return false;
            }
        }
    }

    @FunctionalInterface
    public interface IFloatEqual {
        boolean apply(String value, float n);
    }
    public static class  FloatEqual implements IFloatEqual {
        @Override
        public boolean apply(String value, float n) {
            if (value == null || value.isEmpty()) {
                return false; // Retourne false si la chaîne est nulle ou vide
            }

            try {
                // Vérifie si la valeur est un float
                float v = Float.parseFloat(value);
                return v == n;
            } catch (NumberFormatException e) {
                return false; // Retourne false si ce n'est pas un entier valide
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
                if(value != null && !value.isEmpty() && list != null && !list.isEmpty()){
                    String[] l = list.split(",\\s*");
                    for(String s : l){
                        if(value.equals(s)) return true;
                    }
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

    @FunctionalInterface
    public interface IEqual {
        Boolean apply(String value, String s);
    }
    public static class  Equal implements IEqual {
        @Override
        public Boolean apply(String value, String s) {
            try {
                if(value != null && !value.isEmpty() && s != null && !s.isEmpty())
                    return s.equals(value);
                return false;
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de Equal : " + e.getMessage());
                return false;
            }
        }
    }




    public static class  IsUppercase implements Function<String,Boolean> {
        @Override
        public Boolean apply(String value) {
            try {
                if (value == null || value.isEmpty()) {
                    return false; // Une chaîne vide ou null n'est pas considérée comme en majuscules
                }

                for (char c : value.toCharArray()) {
                    if (Character.isLetter(c) && !Character.isUpperCase(c)) {
                        return false; // Si une lettre n'est pas en majuscule, retourner false
                    }
                }
                return true;
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de IsUppercase : " + e.getMessage());
                return false;
            }
        }
    }

    public static class  IsLowercase implements Function<String,Boolean> {
        @Override
        public Boolean apply(String value) {
            try {
                if (value == null || value.isEmpty()) {
                    return false; // Une chaîne vide ou null n'est pas considérée comme en majuscules
                }

                for (char c : value.toCharArray()) {
                    if (Character.isLetter(c) && !Character.isLowerCase(c)) {
                        return false; // Si une lettre n'est pas en minuscule, retourner false
                    }
                }
                return true;
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de IsLowercase : " + e.getMessage());
                return false;
            }
        }
    }


    public static class  BeginUpperCase implements Function<String,Boolean> {
        @Override
        public Boolean apply(String value) {
            try {
                if (value == null || value.isEmpty()) {
                    return false; // Une chaîne vide ou null n'est pas considérée comme en majuscules
                }
                char[] chars = value.toCharArray();
                return Character.isLetter(chars[0]) && Character.isUpperCase(chars[0]);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'évaluation de IsLowercase : " + e.getMessage());
                return false;
            }
        }
    }


}
