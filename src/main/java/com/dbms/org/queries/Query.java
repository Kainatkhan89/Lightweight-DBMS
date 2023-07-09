package com.dbms.org.queries;

import com.dbms.org.auth.User;

public interface Query {

    void parse(String query, User current_user, boolean is_transaction);
}
