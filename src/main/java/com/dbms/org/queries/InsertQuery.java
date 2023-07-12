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

    /**
     * Represents an insertion operation, specifying the table name and field values to insert.
     */
    public static class Insertion {
        String tableName;
        Map<String, String> fieldValues;

        /**
         * Constructs an Insertion object with the specified table name and field values.
         *
         * @param tableName    The name of the table to insert into.
         * @param fieldValues  The map of field names and corresponding values to insert.
         */
        Insertion(String tableName, Map<String, String> fieldValues) {
            this.tableName = tableName;
            this.fieldValues = fieldValues;
        }
    }

    /**
     * Parses an INSERT query and performs the corresponding actions.
     *
     * @param query The INSERT query to parse.
     * @param current_user The current user executing the query.
     * @param is_transaction Indicates whether the query is part of a transaction.
     * @throws IllegalArgumentException If the query is invalid.
     */
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

    /**
     * Validates the data to be inserted into a table.
     *
     * @param insertion The Insertion object containing the table name and field values.
     * @param table The table metadata.
     * @return True if the data is valid for insertion, false otherwise.
     * @throws IllegalArgumentException If the table names do not match, a field does not exist in the table,
     * a null value is provided for a NOT NULL field, or a duplicate value is provided
     * for a UNIQUE field.
     */
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

    /**
     * Checks if a value is unique for a given field in a table.
     *
     * @param value The value to check for uniqueness.
     * @param table The table metadata.
     * @param fieldName The name of the field to check for uniqueness.
     * @return True if the value is unique, false otherwise.
     * @implNote In a real-world application, this method would typically query the database to check for uniqueness.
     *  However, in this example, we'll just return true to indicate that the value is unique.
     */
    public static boolean isUnique(String value, Table table, String fieldName) {
        // In a real-world application, you would need to check the database to determine if the value is unique.
        // For simplicity, we'll just return true in this example.
        return true;
    }
}
