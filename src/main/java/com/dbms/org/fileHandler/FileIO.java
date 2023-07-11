package com.dbms.org.fileHandler;

import java.io.FileNotFoundException;

public interface FileIO {

    public String[] fileReader(String path);

    boolean fileWriter(String path, String[] userInfo) ;
}
