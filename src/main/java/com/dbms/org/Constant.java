package com.dbms.org;

import java.nio.file.Paths;

/**
 * The Constant class defines various constants used in the application.
 */
public class Constant {
    /**
     * The parent directory path.
     */
    public final static String PARENT_DIR = System.getProperty("user.dir");

    /**
     * The data directory path.
     */
    public final static String DATA_DIR = Paths.get(PARENT_DIR, "data").toString();

    /**
     * The authentication directory path.
     */
    public final static String AUTH_DIR_PATH = Paths.get(DATA_DIR, "auth").toString();

    /**
     * The database directory path.
     */
    public final static String DB_DIR_PATH = Paths.get(DATA_DIR, "db").toString();

    /**
     * The path of the authentication file.
     */
    public final static String AUTH_FILE_PATH = Paths.get(AUTH_DIR_PATH, "user_pass.txt").toString();

    /**
     * The path of the security question and answer file.
     */
    public final static String AUTH_QA_FILE_PATH = Paths.get(AUTH_DIR_PATH, "user_qa.txt").toString();

    /**
     * The suffix for database metadata files.
     */
    public final static String DB_META_SUFFIX = "_meta.txt";

    /**
     * The suffix for database data files.
     */
    public final static String DB_DATA_SUFFIX = "_data.txt";

    /**
     * The suffix for temporary database metadata files.
     */
    public final static String DB_TEMP_META_SUFFIX = "_meta_temp.txt";

    /**
     * The suffix for temporary database data files.
     */
    public final static String DB_TEMP_DATA_SUFFIX = "_data_temp.txt";

    /**
     * The secret key used for encryption.
     */
    public final static String SECRET_KEY = "mysecretkey12345";
}
