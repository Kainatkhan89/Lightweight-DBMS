package com.dbms.org.queries;

import com.dbms.org.Constant;
import com.dbms.org.Utils;
import com.dbms.org.auth.AuthFile;
import com.dbms.org.auth.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.dbms.org.queries.Metadata.Field;
import com.dbms.org.queries.Metadata.Table;

public class SelectQuery extends Query  {

    static class Selection {
        String tableName;
        String[] fields;

        Selection(String tableName, String[] fieldValues) {
            this.tableName = tableName;
            this.fields = fieldValues;
        }
    }

    public static void parse(String query, User current_user, boolean is_transaction) {

        String[] condition = new String[0];
        String[] fieldStrings = new String[0];
        String conditionField = null;
        String conditionValue = null;

        String regex = "SELECT\\s+(.*?)\\s+FROM\\s+(\\w+)(?:\\s+WHERE\\s+(.*))?";
        Pattern selectPattern = Pattern.compile(regex,Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher matcher = selectPattern.matcher(query);

        if (matcher.find()) {
            String fieldsString = matcher.group(1);
            fieldStrings = fieldsString.split(",\\s*");

            tableName = matcher.group(2);
            if (tableName == null) {
                throw new IllegalArgumentException("Invalid query: " + query);
            }

            if(matcher.group(3)!=null){
                String[] conditionStrings = matcher.group(3).replaceAll(";", "").trim().split("(?i)\\bAND\\b|\\bOR\\b");

                //For this Assignment we need only one condition, so I am selecting first one only
                String firstCondition = conditionStrings[0];
                condition = firstCondition.split("=");
                conditionField = condition[0].trim();
                conditionValue = condition[1].trim();
            }
        }
        Selection selection = new Selection(tableName,fieldStrings);
        fields = parseTableMetaData(tableName);
        Table table = new Table(tableName, fields);


        // return boolean if successful
        boolean validatedSelectedData = validateSelection(selection, table, conditionField);


        if(validatedSelectedData && !is_transaction){
            Utils.print(printResults(table, fieldStrings, conditionField,conditionValue));
            Utils.print("\nResults printed successfully.");
        }
        else if(validatedSelectedData && is_transaction){
            // transaction part here
        }
        else {
            Utils.error("Columns are not valid");
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

    // This function is just validating if all the columns are valid in query and conditions
    public static boolean validateSelection(Selection selection, Table table, String conditionField) {
        if (!selection.tableName.equalsIgnoreCase(table.table_name)) {
            throw new IllegalArgumentException("Table names do not match");
        }

        boolean conditionFieldExists = true;

        if (selection.fields.length == 1 && selection.fields[0].equals("*")) {
            // Handle the case when all fields are selected
            if(conditionField != null)
                conditionFieldExists = table.fields.stream().anyMatch(field -> field.name.contains(conditionField));
            return conditionFieldExists;
        } else {
            // Handle the case when specific fields are selected
            boolean allFieldsExist = Arrays.stream(selection.fields)
                    .allMatch(item -> table.fields.stream().anyMatch(obj -> obj.name.equals(item)));

            if(conditionField != null)
                conditionFieldExists = table.fields.stream().anyMatch(field -> field.name.contains(conditionField));

            if (!conditionFieldExists) {
                throw new IllegalArgumentException("Condition field does not exist in the table");
            }

            return allFieldsExist && conditionFieldExists;
        }
    }
    static String printResults(Table table, String[] selectedColumns, String conditionColumn, String conditionValue) {
        StringBuilder results = new StringBuilder();

        // Read from table
        AuthFile file = new AuthFile();
        File dataFile = new File(Paths.get(Constant.DB_DIR_PATH, table.table_name + Constant.DB_DATA_SUFFIX).toUri());
        String[] fileData = file.fileReader(dataFile.getPath());
        List<String> filteredData = new ArrayList<>();

        // Determine the column names based on selectedColumns
        if (selectedColumns.length == 1 && selectedColumns[0].equals("*")) {
            selectedColumns = table.fields.stream().map(field -> field.name).toArray(String[]::new);
        }

        filteredData.add(String.join(", ", selectedColumns));

        if (conditionColumn != null && conditionValue != null) {
            int conditionIndex = IntStream.range(0, table.fields.size())
                    .filter(i -> table.fields.get(i).name.equals(conditionColumn))
                    .findFirst()
                    .orElse(-1);

            if (conditionIndex == -1) {
                // Condition column not found
                return results.toString();
            }

            for (String data : fileData) {
                String[] values = data.split(",");
                if (values.length > conditionIndex && values[conditionIndex].equals(conditionValue)) {
                    List<String> selectedValues = new ArrayList<>();
                    for (String column : selectedColumns) {
                        int columnIndex = IntStream.range(0, table.fields.size())
                                .filter(i -> table.fields.get(i).name.equals(column))
                                .findFirst()
                                .orElse(-1);
                        if (columnIndex != -1 && values.length > columnIndex) {
                            selectedValues.add(values[columnIndex]);
                        }
                    }
                    filteredData.add(String.join(", ", selectedValues));
                }
            }
        } else {
            // No condition provided, include all data
            for (String data : fileData) {
                String[] values = data.split(",");
                List<String> selectedValues = new ArrayList<>();
                for (String column : selectedColumns) {
                    int columnIndex = IntStream.range(0, table.fields.size())
                            .filter(i -> table.fields.get(i).name.equals(column))
                            .findFirst()
                            .orElse(-1);
                    if (columnIndex != -1 && values.length > columnIndex) {
                        selectedValues.add(values[columnIndex]);
                    }
                }
                filteredData.add(String.join(", ", selectedValues));
            }
        }

        // Construct the results string
        results.append(String.join("\n", filteredData));

        return results.toString();
    }
}
