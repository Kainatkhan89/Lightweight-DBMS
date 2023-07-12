package com.dbms.org.queries;

import com.dbms.org.Constant;
import com.dbms.org.Utils;
import com.dbms.org.auth.AuthFile;
import com.dbms.org.auth.User;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.dbms.org.queries.Metadata.Table;

public class DeleteQuery extends Query {

    public static void parse(String query, User current_user, boolean is_transaction) {

        String conditionField = null;
        String conditionValue = null;
        List<Metadata.Field> fields = new ArrayList<Metadata.Field>();
        String[] condition = new String[0];

        // Parse table name and condition from the DELETE query
        Pattern deletePattern = Pattern.compile("DELETE\\s+FROM\\s+(\\w+)(?:\\s+WHERE\\s+(.*))?", Pattern.DOTALL);
        Matcher matcher = deletePattern.matcher(query);

        if (matcher.find()) {
            tableName = matcher.group(1);

            try{
                if(matcher.group(2)!=null) {
                    String[] conditionStrings = matcher.group(2).replaceAll(";", "").trim().split("(?i)\\bAND\\b|\\bOR\\b");
                    //For this Assignment we need only one condition, so I am selecting first one only
                    String firstCondition = conditionStrings[0];
                    condition = firstCondition.split("=");
                    conditionField = condition[0].trim();
                    conditionValue = condition[1].trim();
                }

            }catch (Exception e){
                Utils.warning(" All data will be deleted !!");
            }
        }

        fields = parseTableMetaData(tableName);
        Table table = new Table(tableName, fields);

        // return boolean if successful
        boolean validatedSelectedData = validateDeletion( table, conditionField);


        if(validatedSelectedData && !is_transaction){
            deleteData(table, conditionField,conditionValue);
            Utils.print("\nRecords deleted successfully.");
        }
        else if(validatedSelectedData && is_transaction){
            // transaction part here
        }
        else {
            Utils.error("Columns are not valid");
        }
    }

    public static boolean validateDeletion(Metadata.Table table,  String conditionField) {
        if (!tableName.equalsIgnoreCase(table.table_name)) {
            throw new IllegalArgumentException("Table names do not match");
        }

        boolean conditionFieldExists = true;
        if(conditionField != null)
            conditionFieldExists = table.fields.stream().anyMatch(field -> field.name.contains(conditionField));
        return conditionFieldExists;
    }

    public static void deleteData(Table table, String conditionColumn, String conditionValue) {
        String filePath = Paths.get(Constant.DB_DIR_PATH, table.table_name + Constant.DB_DATA_SUFFIX).toString();

        // Read from table
        AuthFile file = new AuthFile();
        File dataFile = new File(filePath);
        String[] fileData = file.fileReader(dataFile.getPath());

        List<String> updatedLines = new ArrayList<>();

        // Process data rows and remove lines that match the condition or remove all lines if condition is null
        if(conditionColumn != null && conditionValue != null){
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

                // Remove the line if the condition matches or no condition is provided
                if (!conditionMatches) {
                    // Add the line to the updated lines list
                    updatedLines.add(line);
                }
            }
        }

        // Write the updated lines back to the file
        file.fileOverrideWriter(filePath, updatedLines.toArray(new String[0]));
        System.out.println("Data updated successfully");
    }


}
