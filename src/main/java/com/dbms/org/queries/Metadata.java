package com.dbms.org.queries;

import java.util.List;
import java.util.stream.Collectors;

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
            return name + "," + type + "," + constraint;
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
            String result = fields.stream()
                    .map(n -> String.valueOf(n))
                    .collect(Collectors.joining("\n","",""));
            return "Table Name: \n" + table_name + "\nFields:\n" + result;
        }
    }
}
