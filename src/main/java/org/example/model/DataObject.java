package org.example.model;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

public class DataObject {
    private Map<String , Object> fields ;

    public DataObject(){
        this.fields = new HashMap<>();
    }

    // Ajouter un champ dynamiquement
    public void addField(String fieldName, Object value) {
        this.fields.put(fieldName, value);
    }

    // Récupérer la valeur d'un champ
    public Object getFieldValue(String fieldName) {
        return this.fields.get(fieldName);
    }

    // Récupérer tous les champs
    public Map<String, Object> getFields() {
        return fields;
    }


}
