package com.dbms.org.auth;
import com.dbms.org.fileHandler.FileIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class AuthFile implements FileIO {

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
                if (res[1] == req[1] && Encryption.decrypt(res[2].toString()) == req[2]){
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
    public boolean fileWriter(String path, String info) {
        try (PrintWriter out = new PrintWriter("filename.txt")) {
            out.println(info);
        } catch (FileNotFoundException ex){

        }
        return false;
    }


}
