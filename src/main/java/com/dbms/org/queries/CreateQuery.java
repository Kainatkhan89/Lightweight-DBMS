package com.dbms.org.queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dbms.org.Utils;
import com.dbms.org.auth.User;
import com.dbms.org.queries.Metadata.Field;
import com.dbms.org.queries.Metadata.Table;

public class CreateQuery {

    private static final Pattern TABLE_PATTERN = Pattern.compile("CREATE TABLE (\\w+) \\((.*)\\);", Pattern.CASE_INSENSITIVE);
    private static final Pattern FIELD_PATTERN = Pattern.compile("(\\w+) (\\w+\\(.*?\\)|\\w+)", Pattern.CASE_INSENSITIVE);


    /*
     * Assumes that the query is of the form: 
     *  
     * ```sql
     *  CREATE TABLE Persons (
     *      PersonID int,
     *      LastName varchar(255),
     *      FirstName varchar(255),
     *      Address varchar(255),
     *      City varchar(255)
     *  );
     * ```sql
     * 
     * @param query: The query to be parsed
     * @param current_user: The current user
     * @param is_transaction: Whether the query is part of a transaction
     * @return void
     */
    public static void parse(String query, User current_user, boolean is_transaction) {

        if (!query.toLowerCase().contains("table")) {
            Utils.error("Invalid query. Please check the syntax.");
            return;
        }

        String tableName = null;
        List<Field> fields = new ArrayList<>();
        Pattern createTablePattern = Pattern.compile("CREATE TABLE (\\w+) \\((.*)\\);", Pattern.DOTALL);
        Matcher matcher = createTablePattern.matcher(query);
        if (matcher.find()) {
            tableName = matcher.group(1);
            String[] fieldStrings = matcher.group(2).split(",");
            for (String fieldString : fieldStrings) {
                String[] parts = fieldString.trim().split(" ");
                String name = parts[0];
                String type = parts[1];
                String constraint = parts.length > 2 ? String.join(" ", Arrays.copyOfRange(parts, 2, parts.length)) : "";
                fields.add(new Field(name, type, constraint));
            }
        }

        if (tableName == null) {
            Utils.error("Invalid query. Please check the syntax.");
            return;
        }

        Table table = new Table(tableName, fields);

        Utils.print("Table created successfully.");
        Utils.print(table.toString());
    }
}
