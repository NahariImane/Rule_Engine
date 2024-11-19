package org.example.model;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

public class DataObject {
    private Map<String , Object> fields = new HashMap<>();

   /* public DataObject(){
        this.fields = new HashMap<>();
    }*/

    public void setField(String fieldName, Object value) {
        fields.put(fieldName, value);
    }

    public Object getField(String fieldName) {
        //return fields.get(fieldName);
        return fields.getOrDefault(fieldName, null);
    }
    public Map<String, Object> getFields() {
        return fields;
    }
    @Override
    public String toString() {
        return "DataObject[fields=" + fields + "]";
    }

}
