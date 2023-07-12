package com.dbms.org.queries;

import com.dbms.org.Constant;
import com.dbms.org.auth.AuthFile;
import com.dbms.org.auth.User;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

import com.dbms.org.queries.Metadata.Field;
import com.dbms.org.queries.Metadata.Table;

public class InsertQuery extends Query {

    static class Insertion {
        String tableName;
        Map<String, String> fieldValues;

        Insertion(String tableName, Map<String, String> fieldValues) {
            this.tableName = tableName;
            this.fieldValues = fieldValues;
        }
    }

    public static void parse(String query, User current_user, boolean is_transaction) {

        //String tableName = null;
        
        Pattern insertPattern = Pattern.compile("INSERT INTO (\\w+) \\((.*?)\\)\\s*VALUES \\((.*?)\\);", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher matcher = insertPattern.matcher(query);
        
        if (matcher.find()) {
            tableName = matcher.group(1);
            if (tableName == null) {
                throw new IllegalArgumentException("Invalid query: " + query);
            }
            
            String[] fieldNames = matcher.group(2).split(",");
            String[] values = matcher.group(3).split(",");
            
            Map<String, String> tempValues = new HashMap<>();
            for (int i = 0; i < fieldNames.length; i++) {
                tempValues.put(fieldNames[i].trim(), values[i].trim());
            }
            
            fields = parseTableMetaData(tableName);
            for (Field field : fields) {
                String value = tempValues.get(field.name);
                fieldValues.put(field.name, value);
            }
        }

        Table table = new Table(tableName, fields);
        Insertion insertion = new Insertion(tableName, fieldValues);

        // return boolean if successful
        boolean validatedInsertedData = validateInsertion(insertion, table);

        if(validatedInsertedData && !is_transaction){
            // add to table
            AuthFile file = new AuthFile();
            File dataFile = new File(Paths.get(Constant.DB_DIR_PATH, tableName+Constant.DB_DATA_SUFFIX).toUri());
            String[] array = fieldValues.values().toArray(new String[0]);
            file.fileWriter(dataFile.getPath(),array);
        }
    }

    public static boolean validateInsertion(Insertion insertion, Table table) {
        if (!insertion.tableName.equalsIgnoreCase(table.table_name)) {
            throw new IllegalArgumentException("Table names do not match");
        }

        for (Map.Entry<String, String> entry : insertion.fieldValues.entrySet()) {
            String fieldName = entry.getKey();
            String value = entry.getValue();
            Field field = table.fields.stream().filter(f -> f.name.equalsIgnoreCase(fieldName)).findFirst().orElse(null);
            
            if (field == null) {
                throw new IllegalArgumentException("Field " + fieldName + " does not exist in the table " + table.table_name);
            }

            if ("NOT NULL".equalsIgnoreCase(field.constraint) && value == null) {
                throw new IllegalArgumentException("Null value for NOT NULL field " + fieldName);
            }

            if ("UNIQUE".equalsIgnoreCase(field.constraint) && !isUnique(value, table, fieldName)) {
                throw new IllegalArgumentException("Duplicate value for UNIQUE field " + fieldName);
            }

            // For simplicity, not validating type or REFERENCES constraint.
        }
        return true;
    }

    public static boolean isUnique(String value, Table table, String fieldName) {
        // In a real-world application, you would need to check the database to determine if the value is unique.
        // For simplicity, we'll just return true in this example.
        return true;
    }
}
