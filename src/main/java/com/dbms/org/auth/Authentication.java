package com.dbms.org.auth;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.dbms.org.Constant;
import com.dbms.org.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Represents the authentication mechanism for user login.
 */
public class Authentication {
    /**
     * The ID of the authenticated user.
     */
    static public String userID;

    /**
     * The username of the authenticated user.
     */
    static public String userName;

    /**
     * The password of the authenticated user.
     */
    static public String password;

    /**
     * The AuthFile object used for file operations.
     */
    static AuthFile authfile = new AuthFile();

    /**
     * Authenticates the user by prompting for username and password.
     *
     * @return The User object representing the authenticated user, or null if authentication fails.
     */
    public static User login() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter username:");
        userName = input.nextLine();
        System.out.print("\nEnter new password:");
        password = input.nextLine();

        String[] userInfo = null;
        String[] usersData = authfile.fileReader(Constant.AUTH_FILE_PATH);
        for (String info : usersData) {
            String[] res = info.split(",");
            if (res[1].equals(userName) && Encryption.decrypt(res[2]).equals(password)) {
                userInfo = res;
            }
        }

        if (userInfo != null) {
            userID = userInfo[0];
            User user = new User(Integer.parseInt(userInfo[0]), userInfo[1]);
            String[] securityQA = fileReader(Constant.AUTH_QA_FILE_PATH, userID);
            System.out.println(securityQA[1]);
            String securityAnswer = input.nextLine();
            if (securityAnswer.equalsIgnoreCase(securityQA[2])) {
                return user;
            } else {
                System.out.println("Security answer is incorrect!\nLogin failed!");
                System.exit(0);
                return null;
            }
        } else {
            System.out.println("User not found");
            System.exit(0);
            return null;
        }
    }

    /**
     * Reads the specified file and retrieves the information associated with the given info.
     *
     * @param path The path to the file.
     * @param info The information to search for in the file.
     * @return An array of strings representing the information found, or an empty array if not found.
     */
    public static String[] fileReader(String path, String info) {
        try {
            String[] req = info.split(",");
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] res = line.split(",");
                if (res[0].equals(req[0])) {
                    scanner.close();
                    return res;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
        return new String[0];
    }
}

