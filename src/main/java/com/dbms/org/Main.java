package com.dbms.org;

import com.dbms.org.auth.Authentication;
import com.dbms.org.auth.CreateUser;
import com.dbms.org.auth.User;
import com.dbms.org.queries.Query;

import java.util.Scanner;

import static java.lang.System.in;

/**
 * Hello world!
 *
 */
public class Main
{
    public static void main( String[] args )
    {
        Scanner input = new Scanner(System.in);
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

            String query = input.nextLine();
            String queryType = query.split(" ")[0];

        }
    }
}
