package com.dbms.org.auth;

import com.dbms.org.Constant;

public class Authentication {
    public String userID;
    public String userName;
    public String password;
    AuthFile file = new AuthFile();
    public static User login(){
        String userName; String password;
        String[] userInfo = file.fileReader(Constant.AUTH_FILE_PATH, userName+","+Encryption.encrypt(password));
        User user = new User(Integer.parseInt(userInfo[0]), userInfo[1]);


        return user;
    }
 }
