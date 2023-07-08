package com.dbms.org.auth;

import com.dbms.org.Constant;
import com.dbms.org.Utils;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Scanner;

public class CreateUser {

    private static AuthFile authFile = new AuthFile();

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

    public static boolean addSecurityQuestion(int userID, String securityQuestion, String securityAnswer) {
        String[] userInfo = {String.valueOf(userID), securityQuestion, securityAnswer};
        return authFile.fileWriter(Constant.AUTH_QA_FILE_PATH, userInfo);
    }

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

        Utils._print("\nEnter new password: ");
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
