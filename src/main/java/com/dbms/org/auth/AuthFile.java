package com.dbms.org.auth;
import com.dbms.org.Constant;
import com.dbms.org.fileHandler.FileIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.dbms.org.Utils;
import java.io.FileWriter;
import java.io.IOException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a file handler for authentication-related operations.
 */
public class AuthFile implements FileIO {

    /**
     * Finds the user with the specified username in the authentication file.
     *
     * @param username The username to search for.
     * @return An array of strings representing the user information if found, or an empty array if not found.
     */
    public String[] findUserName(String username) {
        File file = new File(Constant.AUTH_FILE_PATH);

        // Create necessary directories if they don't exist
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        if (!file.exists()) {
            Utils.log("No users found! Creating new user...");
            try {
                file.createNewFile();
            } catch (Exception e) {
                Utils.error("Error creating file: " + e.getMessage());
            }
            return new String[]{}; // return empty array
        }

        // if file exists, then read file and return array. file format: id,username,password
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] res = line.split(",");
                if (res[1].equals(username)) {
                    scanner.close();
                    return res;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            Utils.error("File not found: " + e.getMessage());
        }
        return new String[]{};
    }

    @Override
    public String[] fileReader(String path) {
        try {
            File file = new File(path);
            List<String> fileData = new ArrayList<>();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                fileData.add(line);
            }
            String[] res = new String[0];
            res = fileData.toArray(res);
            scanner.close();
            return res;
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
        return new String[0];
    }

    @Override
    public boolean fileWriter(String path, String[] userInfo) {
        try {
            FileWriter fileWriter = new FileWriter(path, true); // Pass 'true' to enable appending
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(String.join(",", userInfo));
            printWriter.close();
            return true;
        } catch (IOException e) {
            Utils.error("An error occurred while appending to the file: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean fileOverrideWriter(String path, String[] userInfo) {
        try {
            FileWriter fileWriter = new FileWriter(path, false); // Pass 'false' to disable appending and override the file
            PrintWriter printWriter = new PrintWriter(fileWriter);
            for (String data : userInfo) {
                printWriter.println(data);
            }
            printWriter.close();
            return true;
        } catch (IOException e) {
            Utils.error("An error occurred while writing to the file: " + e.getMessage());
        }
        return false;
    }
}

