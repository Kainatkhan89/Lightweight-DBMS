package com.dbms.org.auth;

import com.dbms.org.Constant;
import com.dbms.org.Utils;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Scanner;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Scanner;

/**
 * Represents a user creation utility.
 */
public class CreateUser {

    private static AuthFile authFile = new AuthFile();

    /**
     * Creates a new user with the specified username and password.
     *
     * @param username The username of the new user.
     * @param password The password of the new user.
     * @return The ID of the created user if successful, or -1 if an error occurred.
     */
    private static int createNewUser(String username, String password) {
        int userID = -1;
        try {
            LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(Constant.AUTH_FILE_PATH));
            lineNumberReader.skip(Long.MAX_VALUE);
            userID = lineNumberReader.getLineNumber() + 1;
            lineNumberReader.close();
        } catch (IOException e) {
            Utils.error("Error reading file: " + e.getMessage());
            return -1;
        }

        String encryptedPassword = Encryption.encrypt(password);
        String[] userInfo = {String.valueOf(userID), username, encryptedPassword};
        boolean is_success = authFile.fileWriter(Constant.AUTH_FILE_PATH, userInfo);

        return is_success ? userID : -1;
    }

    /**
     * Adds a security question and answer for the specified user.
     *
     * @param userID           The ID of the user.
     * @param securityQuestion The security question to add.
     * @param securityAnswer   The answer to the security question.
     * @return True if the security question and answer were added successfully, false otherwise.
     */
    public static boolean addSecurityQuestion(int userID, String securityQuestion, String securityAnswer) {
        String[] userInfo = {String.valueOf(userID), securityQuestion, securityAnswer};
        return authFile.fileWriter(Constant.AUTH_QA_FILE_PATH, userInfo);
    }

    /**
     * Allows a user to sign up by providing a username, password, security question, and security answer.
     *
     * @return True if the user signup was successful, false otherwise.
     */
    public static boolean signup() {

        Scanner input = new Scanner(System.in);
        Utils._print("Enter username: ");
        String userName = input.nextLine();

        String[] user_info = authFile.findUserName(userName);
        if (user_info.length > 0) {
            Utils.error("Username already exists. Please try again.");
            input.close();
            return false;
        }

        Utils._print("Enter new password: ");
        String password = input.nextLine();

        Utils._print("Enter question: ");
        String securityQuestion = input.nextLine();

        Utils._print("Enter answer: ");
        String securityAnswer = input.nextLine();

        input.close();

        int userID = createNewUser(userName, password);
        if (userID == -1) {
            Utils.error("Error creating user.");
            return false;
        }
        return addSecurityQuestion(userID, securityQuestion, securityAnswer);
    }
}

