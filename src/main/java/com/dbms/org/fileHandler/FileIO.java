package com.dbms.org.fileHandler;

public interface FileIO {

    public String[] fileReader(String path);

    public boolean fileWriter(String path);
}
