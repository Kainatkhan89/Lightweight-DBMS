package com.dbms.org.auth;
import com.dbms.org.fileHandler.FileIO;

public class AuthFile implements FileIO {
    /**
     * @param path
     * @return
     */
    @Override
    public String[] fileReader(String path) {

        return new String[0];
    }

    /**
     * @param path
     * @return
     */
    @Override
    public boolean fileWriter(String path) {
        return false;
    }
}
