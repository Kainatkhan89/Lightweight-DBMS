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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.dbms.org.queries.Metadata.Field;
import com.dbms.org.queries.Metadata.Table;

public class SelectQuery implements Query  {

    static class Selection {
        String tableName;
        List<Field> fields;

        Selection(String tableName, List<Field> fieldValues) {
            this.tableName = tableName;
            this.fields = fieldValues;
        }
    }

    public static void parse(String query, User current_user, boolean is_transaction) {
        String tableName = null;
        List<Field> fields = new ArrayList<>();
        String[] conditions = new String[0];

        String pattern = "(?i)\\bSELECT\\b\\s(.*?)\\bFROM\\b\\s(.*?)\\bWHERE\\b(.*)|$";
        Pattern regex = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher matcher = regex.matcher(query);

        if (matcher.find()) {
            String fieldsString = matcher.group(1);
            tableName = matcher.group(2).replaceAll("[\\r\\n]+", "");;
            String conditionsString = matcher.group(3).replaceAll("[\\r\\n]+", "").replaceAll(";", "");;
            if (tableName == null) {
                throw new IllegalArgumentException("Invalid query: " + query);
            }
            String[] fieldStrings = fieldsString.split(",");
            for (String fieldString : fieldStrings) {
                fields.add(new Field(fieldString.trim(),"",""));
            }
            String[] conditionStrings = conditionsString.trim().split("(?i)\\bAND\\b|\\bOR\\b");
            conditions = conditionStrings[0].split("=");
        }
        Selection selection = new Selection(tableName,fields);
        fields = parseTableMetaData(tableName);
        Table table = new Table(tableName, fields);

        String cond = conditions[0].trim();
        // return boolean if successful
        boolean validatedSelectedData = validateSelection(selection, table, cond);

        if(validatedSelectedData && !is_transaction){
            int conditionIndex = IntStream.range(0, table.fields.size())
                    .filter(i -> table.fields.get(i).name.equals(cond)).findFirst().orElse(-1);
                   
            Utils.print(printResults(table, conditionIndex,conditions[1].trim()));
            Utils.print("\nResults printed successfully.");
        }
        else if(validatedSelectedData && is_transaction){
            // transaction part here
        }
        else{
            Utils.error("Something went wrong");
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
    public static boolean validateSelection(Selection selection, Table table, String condition) {
        if (!selection.tableName.equalsIgnoreCase(table.table_name)) {
            throw new IllegalArgumentException("Table names do not match");
        }

        if(selection.fields.stream().anyMatch(field -> field.name.contains("*"))){
            if(selection.fields.size()==1)
             return table.fields.stream().anyMatch(field -> field.name.contains(condition));
            else
                selection.fields.remove(selection.fields.stream()
                        .filter(field -> field.name.contains("*"))
                        .findFirst());
        }
        return table.fields.stream().allMatch(field1 -> selection.fields.stream().anyMatch(field2 -> field2.name.equals(field1.name)))
                 && table.fields.stream().anyMatch(field -> field.name.contains(condition));
    }
    static String printResults(Table table, int conditionIndex, String condition){
        String results = "";

        // read from table
        AuthFile file = new AuthFile();
        File dataFile = new File(Paths.get(Constant.DB_DIR_PATH, table.table_name+Constant.DB_DATA_SUFFIX).toUri());
        String[] fileData = file.fileReader(dataFile.getPath());
        String[] filteredData = new String[0];
        List<String> where = new ArrayList<String>();
        where.add(table.fields.stream()
                .map(field -> field.name)
                .collect(Collectors.joining(", ")));
        for (String data : fileData){
            String[] values = data.split(",");
            if(values[conditionIndex].equals(condition)){
                where.add(data);
                filteredData = where.toArray(filteredData);
            }
        }
// check the column names
        StringBuilder sb = new StringBuilder();
        for (String str : filteredData) {
            sb.append(str).append("\n");
        }
        sb.deleteCharAt(sb.length() - 1); // Remove the trailing space
        results=sb.toString();
        return results;
    }
}
