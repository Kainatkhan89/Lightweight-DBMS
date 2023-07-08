package com.dbms.org.auth;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.dbms.org.Constant;

public class Authentication {
    static public String userID;
    static public String userName;
    static public String password;
    static AuthFile authfile = new AuthFile();

    public static User login(){

        Scanner input = new Scanner(System.in);

        System.out.print("Enter username:");
        userName = input.nextLine();
        System.out.print("\nEnter new password:");
        password = input.nextLine();

        String[] userInfo = authfile.fileReader(Constant.AUTH_FILE_PATH, userName+","+password);
        if(userInfo!=null){
            userID=userInfo[0];
            User user = new User(Integer.parseInt(userInfo[0]), userInfo[1]);
            String[] securityQA = fileReader(Constant.AUTH_QA_FILE_PATH, userID);
            System.out.println(securityQA[1]);
            String securityAnswer = input.nextLine();
            if(securityAnswer.toLowerCase().equals(securityQA[2].toLowerCase())){
                return user;
            }else {
                System.out.println("Login failed!");
                return null;
            }
        }
        else{
            System.out.println("User not found");
            return null;
        }

    }
    public static String[] fileReader(String path, String info) {
        try {
            String[] req = info.split(",");
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] res = line.split(",");
                if (res[0].equals(req[0])){
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
