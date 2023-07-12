package com.dbms.org.fileHandler;

import java.io.FileNotFoundException;

public interface FileIO {

    String[] fileReader(String path);

    boolean fileWriter(String path, String[] userInfo) ;

    boolean fileOverrideWriter(String path, String[] userInfo);
}
