package com.dbms.org;

import com.dbms.org.auth.Authentication;
import com.dbms.org.auth.CreateUser;
import com.dbms.org.auth.User;

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

            System.out.println("Welcome to Kainat DBMS. Select the option:\n 1. Login\n2. Signup");
            int userInput;
            try {
                userInput = Integer.parseInt(input.nextLine());
                if (userInput == 1) {
                    System.out.println("Login selected.");
                    current_user = Authentication.login();
                } else if (userInput == 2) {
                    System.out.println("Signup selected.");
                    System.out.println(CreateUser.signup()? "Signup successful":"Something went wrong");
                    System.exit(0);
                } else {
                    System.out.println("Invalid input. Please enter 1 for login or 2 for signup.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }

        }

        



        System.out.println(current_user.isInvalid());
    }
}
