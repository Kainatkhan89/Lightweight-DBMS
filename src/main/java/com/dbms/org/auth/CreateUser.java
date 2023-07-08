package com.dbms.org.auth;

import com.dbms.org.Constant;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Scanner;

public class CreateUser {

    AuthFile authFile = new AuthFile();
    private String userID;
    private String userName;
    private String password;
    private String question;
    private String answer;

    public CreateUser() {
    }
    public CreateUser(String userName, String password, String question, String answer) {
        this.userName = userName;
        this.password = password;
        this.question = question;
        this.answer = answer;
    }

    public static boolean signup() {

        Scanner input = new Scanner(System.in);
         System.out.print("Enter username:");
        String userName = input.nextLine();
        // verify username exists

        System.out.print("\nEnter new password:");
        String password = input.nextLine();

        System.out.println("Enter question");
        String securityQuestion = input.nextLine();

        System.out.println("Enter answer");
        String securityAnswer = input.nextLine();

        System.out.println("User authentication information:");
        System.out.println("Username: " + userName);
        System.out.println("Password: " + password);
        System.out.println("Security Question: " + securityQuestion);
        System.out.println("Security Answer: " + securityAnswer);

        int noOfLines = -1;
        try(LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(Constant.AUTH_FILE_PATH))) {
            lineNumberReader.skip(Long.MAX_VALUE);
            noOfLines = lineNumberReader.getLineNumber() + 1;
        } catch (FileNotFoundException ex){} catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.userID= String.valueOf(noOfLines);
        authFile.fileWriter(Constant.AUTH_FILE_PATH, userID+","+userName+","+Encryption.encrypt(password));
        authFile.fileWriter(Constant.AUTH_QA_FILE_PATH, userID+","+question+","+answer);
        return true;
    }

}
