package com.dbms.org.queries;

import com.dbms.org.Constant;
import com.dbms.org.Utils;
import com.dbms.org.auth.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The Query class represents a base class for different types of queries.
 */
public class Query {
    static String tableName = null;
    static Map<String, String> fieldValues = new LinkedHashMap<>();
    static List<Metadata.Field> fields = new ArrayList<>();

    /**
     * Parses the given query.
     *
     * @param query         the query to parse
     * @param current_user  the current user
     * @param is_transaction indicates if the query is part of a transaction
     */
    public static void parse(String query, User current_user, boolean is_transaction) {
        // Override in subclasses
    }

    /**
     * Parses the metadata of the specified table and returns a list of fields.
     *
     * @param tableName the name of the table
     * @return a list of fields representing the metadata of the table
     */
    public static List<Metadata.Field> parseTableMetaData(String tableName) {
        List<Metadata.Field> fields = new ArrayList<>();

        String metaFilePath = Paths.get(Constant.DB_DIR_PATH, tableName + Constant.DB_META_SUFFIX).toString();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(metaFilePath));
            String line = reader.readLine();
            int lineNumber = 0;
            while (line != null) {
                if (lineNumber > 2) {
                    String[] parts = line.split(",");
                    String name = parts[0];
                    String type = parts[1];
                    String constraint = parts[2];
                    fields.add(new Metadata.Field(name, type, constraint));
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
}

