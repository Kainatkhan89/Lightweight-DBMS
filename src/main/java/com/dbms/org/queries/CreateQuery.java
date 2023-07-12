package com.dbms.org.queries;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dbms.org.Constant;
import com.dbms.org.Utils;
import com.dbms.org.auth.User;
import com.dbms.org.queries.Metadata.Field;
import com.dbms.org.queries.Metadata.Table;

public class CreateQuery extends Query {

    /*
     * Assumes that the query is of the form: 
     *  
     * ```sql
     *  CREATE TABLE Persons (
        PersonID int PRIMARY KEY,
        LastName varchar(255)  NOT NULL,
        FirstName varchar(255) NOT NULL UNIQUE,
        Address varchar(255) UNIQUE,
        City varchar(255),
        customer_id int REFERENCES Customers(id)
        );
     * ```sql
     * 
     * @param query: The query to be parsed
     * @param current_user: The current user
     * @param is_transaction: Whether the query is part of a transaction
     * @return void
     */
    public static void parse(String query, User current_user, boolean is_transaction) {

        Pattern createTablePattern = Pattern.compile("CREATE TABLE (\\w+) \\((.*?)\\);", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher matcher = createTablePattern.matcher(query);

        if (matcher.find()) {
            tableName = matcher.group(1);
            String fieldsString = matcher.group(2).trim();

            String[] fieldStrings = fieldsString.split(",");
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

        Table table = new Table(tableName, fields);

        if(!is_transaction){
            createTableFiles(table);
        }
        Utils.print("Table created successfully.");
        Utils.print(table.toString());
    }

    public static void createTableFiles(Table table){

        File metaFile = new File(Paths.get(Constant.DB_DIR_PATH, table.table_name+Constant.DB_META_SUFFIX).toUri());
        File dataFile = new File(Paths.get(Constant.DB_DIR_PATH, table.table_name+Constant.DB_DATA_SUFFIX).toUri());

        // Create necessary directories if they don't exist
        File parentDir = metaFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

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
