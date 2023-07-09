package com.dbms.org.queries;

import com.dbms.org.Constant;
import com.dbms.org.Utils;
import com.dbms.org.auth.User;

import java.util.ArrayList;
import java.util.List;
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

        Pattern insertPattern = Pattern.compile("INSERT INTO (\\w+) \\((.*?)\\) VALUES \\((.*?)\\);", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher matcher = insertPattern.matcher(query);

        if (matcher.find()) {
            tableName = matcher.group(1);
            String[] fieldNames = matcher.group(2).split(",");
            String[] values = matcher.group(3).split(",");

            for (int i = 0; i < fieldNames.length; i++) {
                fieldValues.put(fieldNames[i].trim(), values[i].trim());
            }
        }

        if (tableName == null) {
            throw new IllegalArgumentException("Invalid query: " + query);
        }

        List<Field> fields = parseTableMetaData(tableName);

        Table persons = new Table(tableName, fields);

        Insertion insertion = new Insertion(tableName, fieldValues);

        // return boolean if successful
        validateInsertion(insertion, persons);

        // add to table
    }

    public static List<Field> parseTableMetaData(String tableName) {
        List<Field> fields = new ArrayList<>();

        String metaFilePath = Paths.get(Constant.DB_DIR_PATH, tableName + Constant.DB_META_SUFFIX).toString();
    
        try {
            BufferedReader reader = new BufferedReader(new FileReader(metaFilePath));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split(",");
                String name = parts[0];
                String type = parts[1];
                String constraint = parts[2];
                fields.add(new Field(name, type, constraint));
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            Utils.error("Error reading table metadata file: " + metaFilePath);
            System.exit(0);
        }

        return fields;
    }

    public static void validateInsertion(Insertion insertion, Table table) {
        if (!insertion.tableName.equalsIgnoreCase(table.table_name)) {
            throw new IllegalArgumentException("Table names do not match");
        }

        for (Map.Entry<String, String> entry : insertion.fieldValues.entrySet()) {
            String fieldName = entry.getKey();
            String value = entry.getValue();
            Field field = table.fields.stream().filter(f -> f.name.equalsIgnoreCase(fieldName)).findFirst().orElse(null);
            
            if (field == null) {
                throw new IllegalArgumentException("Field " + fieldName + " does not exist in the table " + table.name);
            }

            if ("NOT NULL".equalsIgnoreCase(field.constraint) && value == null) {
                throw new IllegalArgumentException("Null value for NOT NULL field " + fieldName);
            }

            if ("UNIQUE".equalsIgnoreCase(field.constraint) && !isUnique(value, table, fieldName)) {
                throw new IllegalArgumentException("Duplicate value for UNIQUE field " + fieldName);
            }

            // For simplicity, not validating type or REFERENCES constraint.
        }
    }

    public static boolean isUnique(String value, Table table, String fieldName) {
        // In a real-world application, you would need to check the database to determine if the value is unique.
        // For simplicity, we'll just return true in this example.
        return true;
    }
}
