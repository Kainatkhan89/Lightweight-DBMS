package com.dbms.org.queries;

import com.dbms.org.auth.User;

import java.util.ArrayList;
import java.util.List;

public interface Query {
    //String tableName = new String();
    //List<Metadata.Field> fields = new ArrayList<>();
    static void  parse(String query, User current_user, boolean is_transaction){};
}
