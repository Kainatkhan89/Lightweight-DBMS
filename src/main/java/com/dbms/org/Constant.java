package com.dbms.org;

import java.nio.file.Paths;

public class Constant {
    public final static String PARENT_DIR = System.getProperty("user.dir");
    public final static String DATA_DIR = Paths.get(PARENT_DIR, "data").toString();
    public final static String AUTH_DIR_PATH = Paths.get(DATA_DIR, "auth").toString();
    public final static String DB_DIR_PATH = Paths.get(DATA_DIR, "db").toString();

    public final static String AUTH_FILE_PATH = Paths.get(AUTH_DIR_PATH, "user_pass.txt").toString();
    public final static String AUTH_QA_FILE_PATH = Paths.get(AUTH_DIR_PATH, "user_qa.txt").toString();

    public final static String DB_META_SUFFIX = "_meta.txt";
    public final static String DB_DATA_SUFFIX = "_data.txt";
    public final static String SECRET_KEY = "mysecretkey12345";
}