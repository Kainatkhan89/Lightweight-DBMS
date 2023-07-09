package com.dbms.org.queries;

import com.dbms.org.Constant;
import com.dbms.org.Utils;
import com.dbms.org.auth.AuthFile;
import com.dbms.org.auth.User;

import java.io.File;
import java.io.BufferedReader;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;
import java.io.FileReader;
import java.io.IOException;

import com.dbms.org.queries.Metadata.Field;
import com.dbms.org.queries.Metadata.Table;

public class InsertQuery {
    
    static class Insertion {
        String tableName;
        Map<String, String> fieldValues;

        Insertion(String tableName, Map<String, String> fieldValues) {
            this.tableName = tableName;
            this.fieldValues = fieldValues;
        }
    }

    public static void parse(String query, User current_user, boolean is_transaction) {

        String tableName = null;
        Map<String, String> fieldValues = new HashMap<>();
        
        Pattern insertPattern = Pattern.compile("INSERT INTO (\\w+) \\((.*?)\\)\\s*VALUES \\((.*?)\\);", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher matcher = insertPattern.matcher(query);
        List<Field> fields = new ArrayList<Field>();
        
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
        
        Table persons = new Table(tableName, fields);
        Insertion insertion = new Insertion(tableName, fieldValues);

        // return boolean if successful
        boolean validatedInsertedData = validateInsertion(insertion, persons);

        if(validatedInsertedData && !is_transaction){
            // add to table
            AuthFile file = new AuthFile();
            File dataFile = new File(Paths.get(Constant.DB_DIR_PATH, tableName+Constant.DB_DATA_SUFFIX).toUri());
            String[] array = fieldValues.values().toArray(new String[0]);
            file.fileWriter(dataFile.getPath(),array);
        }
    }

    public static List<Field> parseTableMetaData(String tableName) {
        List<Field> fields = new ArrayList<>();

        String metaFilePath = Paths.get(Constant.DB_DIR_PATH, tableName + Constant.DB_META_SUFFIX).toString();
    
        try {
            BufferedReader reader = new BufferedReader(new FileReader(metaFilePath));
            String line = reader.readLine();
            int lineNumber = 0;
            while (line != null) {
                if(lineNumber > 2) {
                    String[] parts = line.split(",");
                    String name = parts[0];
                    String type = parts[1];
                    String constraint = parts[2];
                    fields.add(new Field(name, type, constraint));
                }
                lineNumber++;
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            Utils.error("Error reading table metadata file: " + metaFilePath);
            System.exit(0);
        }

        return fields;
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
