package com.dbms.org.queries;

import com.dbms.org.auth.User;

/**
 * The TransactionQuery class is responsible for parsing transaction-related queries.
 */
public class TransactionQuery extends Query {

    /**
     * Parses the transaction query and performs necessary operations.
     *
     * @param query         The transaction query to be parsed.
     * @param current_user  The current user object.
     * @param is_transaction Specifies if the query is part of a transaction.
     */
    public static void parse(String query, User current_user, boolean is_transaction) {

        // Check if the keyword ROLLBACK is present, then don't do anything

        // Check if the keywords COMMIT and ROLLBACK are not present, then throw an error

        // Check for the END TRANSACTION keyword as well

        // Use a LinkedHashMap structure to put all queries in the list
        // The key can be the type of query

    }
}
