package com.dbms.org.queries;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents metadata for database tables and fields.
 */
public class Metadata {

    /**
     * Represents a field in a database table.
     */
    static class Field {
        String name;
        String type;
        String constraint;

        /**
         * Constructs a Field object with the specified name, type, and constraint.
         *
         * @param name       The name of the field.
         * @param type       The type of the field.
         * @param constraint The constraint of the field.
         */
        Field(String name, String type, String constraint) {
            this.name = name;
            this.type = type;
            this.constraint = constraint;
        }

        /**
         * Returns the string representation of the Field object.
         *
         * @return The string representation of the Field object.
         */
        @Override
        public String toString() {
            return name + "," + type + "," + constraint;
        }
    }

    /**
     * Represents a database table with its name and fields.
     */
    static class Table {
        String table_name;
        List<Field> fields;

        /**
         * Constructs a Table object with the specified name and fields.
         *
         * @param name   The name of the table.
         * @param fields The list of fields in the table.
         */
        Table(String name, List<Field> fields) {
            this.table_name = name;
            this.fields = fields;
        }

        /**
         * Returns the string representation of the Table object.
         *
         * @return The string representation of the Table object.
         */
        @Override
        public String toString() {
            String result = fields.stream()
                    .map(n -> String.valueOf(n))
                    .collect(Collectors.joining("\n", "", ""));
            return "Table Name: \n" + table_name + "\nFields:\n" + result;
        }

        /**
         * Gets the index of the column with the specified column name.
         *
         * @param columnName The name of the column.
         * @return The index of the column, or -1 if not found.
         */
        public int getColumnIndex(String columnName) {
            for (int i = 0; i < fields.size(); i++) {
                if (fields.get(i).name.equalsIgnoreCase(columnName)) {
                    return i;
                }
            }
            return -1; // Column not found
        }
    }
}

