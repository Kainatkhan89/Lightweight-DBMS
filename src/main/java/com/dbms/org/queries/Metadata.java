package com.dbms.org.queries;

import java.util.List;

public class Metadata {

    static class Field {
        String name;
        String type;
        String constraint;

        Field(String name, String type, String constraint) {
            this.name = name;
            this.type = type;
            this.constraint = constraint;
        }

        @Override
        public String toString() {
            return "Field Name: " + name + ", Type: " + type + ", Constraint: " + constraint;
        }
    }
    
    static class Table {
        String table_name;
        List<Field> fields;

        Table(String name, List<Field> fields) {
            this.table_name = name;
            this.fields = fields;
        }

        @Override
        public String toString() {
            return "Table Name: " + table_name + ", Fields: " + fields;
        }
    }
}
