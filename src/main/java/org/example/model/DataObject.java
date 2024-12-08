package org.example.model;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

public class DataObject {
    private Map<String , String> fields ;

    public DataObject(){
        this.fields = new HashMap<>();
    }

    // Ajouter un champ dynamiquement
    public void addField(String fieldName, String value) {
        this.fields.put(fieldName, value);
    }

    // Récupérer la valeur d'un champ
    public Object getFieldValue(String fieldName) {
        return this.fields.get(fieldName);
    }

    // Récupérer tous les champs
    public Map<String, String> getFields() {
        return fields;
    }


}
