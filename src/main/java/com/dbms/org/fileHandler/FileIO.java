package com.dbms.org.fileHandler;

import java.io.FileNotFoundException;

/**
 * The FileIO interface defines operations for file input/output.
 * Implementing classes provide implementations for reading from and writing to files.
 */
public interface FileIO {

    /**
     * Reads the contents of a file at the specified path and returns an array of strings representing the lines of the file.
     *
     * @param path the path of the file to read
     * @return an array of strings representing the lines of the file
     */
    String[] fileReader(String path);

    /**
     * Writes the provided userInfo array to a file at the specified path.
     * The method appends the content to the file.
     *
     * @param path     the path of the file to write
     * @param userInfo the array of strings to write to the file
     * @return true if the write operation is successful, false otherwise
     */
    boolean fileWriter(String path, String[] userInfo);

    /**
     * Writes the provided userInfo array to a file at the specified path, overriding any existing content in the file.
     *
     * @param path     the path of the file to write
     * @param userInfo the array of strings to write to the file
     * @return true if the write operation is successful, false otherwise
     */
    boolean fileOverrideWriter(String path, String[] userInfo);
}
