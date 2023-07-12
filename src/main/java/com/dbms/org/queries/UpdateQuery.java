package com.dbms.org.queries;

import com.dbms.org.Constant;
import com.dbms.org.Utils;
import com.dbms.org.auth.AuthFile;
import com.dbms.org.auth.User;
import com.dbms.org.queries.Metadata.Field;
import com.dbms.org.queries.Metadata.Table;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateQuery extends Query {

    static class Updation {
        String tableName;
        Map<String, String> fieldValues;

        Updation(String tableName, Map<String, String> fieldValues) {
            this.tableName = tableName;
            this.fieldValues = fieldValues;
        }
    }

    public static void parse(String query, User current_user, boolean is_transaction) {

        String[] fieldNames = null;
        String[] values = null;
        String conditionField = null;
        String conditionValue = null;
        String[] condition = new String[0];

        // Regex patterns

        Pattern updatePattern = Pattern.compile("UPDATE\\s+(\\w+)\\s+SET\\s+((?:[^;]+?)(?=\\s+WHERE|$))\\s*(?:WHERE\\s+(.*))?",Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher matcher = updatePattern.matcher(query);

        if(!matcher.find()){
            updatePattern = Pattern.compile("UPDATE\\s+(\\w+)\\s+SET\\s+(.*?);", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            matcher = updatePattern.matcher(query);
        }
        // Match columns and values
        if (matcher.find()) {

            tableName = matcher.group(1);
            if (tableName == null) {
                throw new IllegalArgumentException("Invalid query: " + query);
            }
            String setClause = matcher.group(2).replace("\r\n","");

            try{
                if(matcher.group(3)!=null) {
                    String[] conditionStrings = matcher.group(3).replaceAll(";", "").trim().split("(?i)\\bAND\\b|\\bOR\\b");
                    //For this Assignment we need only one condition, so I am selecting first one only
                    String firstCondition = conditionStrings[0];
                    condition = firstCondition.split("=");
                    conditionField = condition[0].trim();
                    conditionValue = condition[1].trim();
                }

            }catch (Exception e){
                Utils.warning(" All records will be updated !!");
            }


            String[] setPairs = setClause.split(",");
            fieldNames = new String[setPairs.length];
            values = new String[setPairs.length];
            for (int i = 0; i < setPairs.length; i++) {
                String[] pair = setPairs[i].trim().split("=");
                fieldNames[i] = pair[0].trim();
                values[i] = pair[1].trim().replaceAll("'", "");
            }

            Map<String, String> tempValues = new HashMap<>();
            for (int i = 0; i < fieldNames.length; i++) {
                tempValues.put(fieldNames[i].trim(), values[i].trim());
            }

            fields = parseTableMetaData(tableName);
            for (Metadata.Field field : fields) {
                String value = tempValues.get(field.name);
                fieldValues.put(field.name, value);
            }
        }

        Table table = new Table(tableName, fields);
        Updation updation = new Updation(tableName, fieldValues);

        // return boolean if successful
        boolean validatedUpdatedData = validateUpdation(updation, table);

        if(validatedUpdatedData && !is_transaction){
            updateData(table,conditionField,conditionValue,fieldNames,values);
            Utils.print("\nData updated successfully.");
        }
        else if(validatedUpdatedData && is_transaction){
            // transaction part here
        }
        else {
            Utils.error("Columns are not valid");
        }
    }

    public static boolean validateUpdation(Updation updation, Table table) {
        if (!updation.tableName.equalsIgnoreCase(table.table_name)) {
            throw new IllegalArgumentException("Table names do not match");
        }

        for (Map.Entry<String, String> entry : updation.fieldValues.entrySet()) {
            String fieldName = entry.getKey();
            String value = entry.getValue();
            Field field = table.fields.stream().filter(f -> f.name.equalsIgnoreCase(fieldName)).findFirst().orElse(null);

            if (field == null) {
                throw new IllegalArgumentException("Field " + fieldName + " does not exist in the table " + table.table_name);
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
    public static void updateData(Table table, String conditionColumn, String conditionValue, String[] updateColumns, String[] updateValues) {
        String filePath = Paths.get(Constant.DB_DIR_PATH, table.table_name + Constant.DB_DATA_SUFFIX).toString();

        // Read from table
        AuthFile file = new AuthFile();
        File dataFile = new File(filePath);
        String[] fileData = file.fileReader(dataFile.getPath());

        List<String> updatedLines = new ArrayList<>();

        // Process data rows and update if condition matches
        for (int i = 0; i < fileData.length; i++) {
            String line = fileData[i];
            String[] values = line.split(",");

            // Check if condition matches
            boolean conditionMatches = true; // Assume condition matches if conditionColumn and conditionValue are null

            if (conditionColumn != null && conditionValue != null) {
                int conditionColumnIndex = table.getColumnIndex(conditionColumn);

                if (conditionColumnIndex == -1) {
                    // Condition column not found
                    System.out.println("Condition column not found");
                    return;
                }

                // Check if condition matches
                conditionMatches = values.length > conditionColumnIndex && values[conditionColumnIndex].trim().equals(conditionValue);
            }

            // Apply the update if the condition matches or no condition is provided
            if (conditionMatches) {
                String[] valuesToUpdate = values.clone();

                // Update the specified columns
                for (int j = 0; j < updateColumns.length; j++) {
                    String updateColumn = updateColumns[j].trim();
                    String updateValue = updateValues[j].trim();
                    int columnIndex = table.getColumnIndex(updateColumn);

                    if (columnIndex == -1) {
                        System.out.println("Update column '" + updateColumn + "' not found");
                        return;
                    }

                    // Update the value
                    valuesToUpdate[columnIndex] = updateValue;
                }

                // Join the updated values into a new line
                String updatedLine = String.join(",", valuesToUpdate);

                // Add the updated line to the list
                updatedLines.add(updatedLine);
            } else {
                // Add the unchanged line to the list
                updatedLines.add(line);
            }
        }

        if (updatedLines.size() > 1) {
            // Write the updated lines back to the file
            file.fileOverrideWriter(filePath, updatedLines.toArray(new String[0]));
            System.out.println("Data updated successfully");
        } else {
            System.out.println("No matching record found");
        }
    }

}
