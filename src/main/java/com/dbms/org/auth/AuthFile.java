package com.dbms.org.auth;
import com.dbms.org.Constant;
import com.dbms.org.fileHandler.FileIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import com.dbms.org.Utils;
import java.io.FileWriter;
import java.io.IOException;

public class AuthFile implements FileIO {

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

    /**
     * @param path
     * @return
     */
    @Override
    public String[] fileReader(String path, String info) {
        try {
            String[] req = info.split(",");
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] res = line.split(",");
                if (res[1].equals(req[0]) &&  Encryption.decrypt(res[2].toString()).equals(req[1])){
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

    /**
     * @param path
     * @return
     */
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
}
