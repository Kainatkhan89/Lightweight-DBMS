package com.dbms.org;

import com.dbms.org.auth.Authentication;
import com.dbms.org.auth.CreateUser;
import com.dbms.org.auth.User;
import com.dbms.org.queries.CreateQuery;
import com.dbms.org.queries.InsertQuery;
import com.dbms.org.queries.SelectQuery;
import com.dbms.org.queries.UpdateQuery;
import com.dbms.org.queries.DeleteQuery;
import com.dbms.org.queries.TransactionQuery;

import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class Main
{
    public static void main( String[] args )
    {
        Scanner input = new Scanner(System.in);
        StringBuilder inputBuilder = new StringBuilder();
        User current_user = new User();

        if(current_user.isInvalid()){

            Utils.lineSeparator();
            
            Utils.print("Welcome to Kainat DBMS.\n");
            Utils.lineSeparator();
            Utils.print("\nSelect the option:\n\n1. Login\n2. Signup\n");
            int userInput;
            try {
                userInput = Integer.parseInt(input.nextLine());
                if (userInput == 1) {
                    Utils.print("Login selected.");
                    current_user = Authentication.login();
                    System.out.println("\nLogin successful !!!\nNow, Enter your queries.\n");
                } else if (userInput == 2) {
                    Utils.print("\nLet's sign you up!\n");
                    Utils.print(CreateUser.signup()? "\nSignup successful !!!\nNow, login with your new user.\n":"\nSomething went wrong. Please try again.\n");
                    System.exit(0);
                } else {
                    Utils.print("Invalid input. Please enter 1 for login or 2 for signup.");
                }
            } catch (NumberFormatException e) {
                Utils.print("Invalid input. Please enter a valid number.");
            }

            if(current_user.isInvalid()){
                Utils.error("Oops! Something went wrong.");
                System.exit(0);
            }

            while (input.hasNextLine()) {
                String line = input.nextLine();
                inputBuilder.append(line);
                inputBuilder.append(System.lineSeparator()); // Add line separator for each line
                if (line.contains(";")) {
                    break;
                }


            }

            input.close();
            String query = inputBuilder.toString();
            String queryType = query.trim().split(" ")[0];

            switch(queryType.toUpperCase()) {
                case "CREATE":
                    CreateQuery.parse(query, current_user, false);
                    break;
                case "INSERT":
                    InsertQuery.parse(query, current_user, false);
                    break;
                case "SELECT":
                    SelectQuery.parse(query, current_user, false);
                    break;
                case "UPDATE":
                    UpdateQuery.parse(query, current_user, false);
                    break;
                case "DELETE":
                    DeleteQuery.parse(query, current_user, false);
                    break;
                case "TRANSACTION":
                    TransactionQuery.parse(query, current_user, true);
                    break;
                default:
                    Utils.error("Invalid query.");
            }

        }
    }
}
