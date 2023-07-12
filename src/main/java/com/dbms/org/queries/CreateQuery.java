package com.dbms.org.queries;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dbms.org.Constant;
import com.dbms.org.Utils;
import com.dbms.org.auth.User;
import com.dbms.org.queries.Metadata.Field;
import com.dbms.org.queries.Metadata.Table;

public class CreateQuery extends Query{

    /**

     * Parses the given query string to create a new table in the database.
     *
     * @param query The CREATE TABLE query string to parse.
     * @param current_user The current user executing the query.
     * @param is_transaction A flag indicating if the query is part of a transaction.
     * @throws IllegalArgumentException If the query syntax is invalid.
     **/
    public static void parse(String query, User current_user, boolean is_transaction) {

        Pattern createTablePattern = Pattern.compile("CREATE TABLE (\\w+) \\((.*?)\\);", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher matcher = createTablePattern.matcher(query);

        if (matcher.find()) {
            tableName = matcher.group(1);
            String fieldsString = matcher.group(2).trim();

            // Split the field definitions into individual fields
            String[] fieldStrings = fieldsString.split(",");
            // Process each field definition and create Field objects
            for (String fieldString : fieldStrings) {
                String[] parts = fieldString.trim().split("\\s+", 3); // split by whitespace into maximum 3 parts
                String name = parts[0];
                String type = parts[1];
                String constraint = parts.length > 2 ? parts[2] : "NONE";
                fields.add(new Field(name, type, constraint));
       }
}

        if (tableName == null) {
            Utils.error("Invalid query. Please check the syntax.");
            return;
        }
        // Create the table object
        Table table = new Table(tableName, fields);

        if(!is_transaction){
            createTableFiles(table);
        }
        // Print success message and table details
        Utils.print("Table created successfully.");
        Utils.print(table.toString());
    }

    /**
     * Creates the meta and data files for the specified table.
     *
     * @param table The table for which to create the files.
     */
    public static void createTableFiles(Table table){

        File metaFile = new File(Paths.get(Constant.DB_DIR_PATH, table.table_name+Constant.DB_META_SUFFIX).toUri());
        File dataFile = new File(Paths.get(Constant.DB_DIR_PATH, table.table_name+Constant.DB_DATA_SUFFIX).toUri());

        // Create necessary directories if they don't exist
        File parentDir = metaFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // Create the meta file if it doesn't exist
        if (!metaFile.exists()) {
            try {
                metaFile.createNewFile();

                FileWriter fileWriter = new FileWriter(metaFile, true); // Pass 'true' to enable appending
                PrintWriter printWriter = new PrintWriter(fileWriter);
                printWriter.println(table.toString());
                printWriter.close();

            } catch (Exception e) {
                Utils.error("Error creating table meta: " + e.getMessage());
            }

        }else{
            Utils.error("Table already exists");
            System.exit(0);
        }

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();

            } catch (Exception e) {
                Utils.error("Error creating table: " + e.getMessage());
            }

        }else{
            Utils.error("Table already exists");
            System.exit(0);
        }
    }
}
