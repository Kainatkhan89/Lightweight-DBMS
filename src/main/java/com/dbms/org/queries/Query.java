package com.dbms.org.queries;

import com.dbms.org.QueryType;
import com.dbms.org.Utils;

public class Query {

    public String parseQuery(String query){
        String[] parsedQuery = query.split(" ");
        return parsedQuery[0].toUpperCase();

       /* if (parsedQuery[0].toUpperCase().equals(QueryType.CREATE)){
// create
        }
        else if (parsedQuery[0].toUpperCase().equals(QueryType.INSERT)){

        }
        else if (parsedQuery[0].toUpperCase().equals(QueryType.UPDATE)){

        }
        else if (parsedQuery[0].toUpperCase().equals(QueryType.DELETE)){

        }
        else if (parsedQuery[0].toUpperCase().equals(QueryType.SELECT)){

        }
        else{
            Utils.print("Some error in query syntax");
        }*/



    }

}
