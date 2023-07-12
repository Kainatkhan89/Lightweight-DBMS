package com.dbms.org;

/**
 * The Utils class provides utility methods for printing messages and logging.
 */
public class Utils {
    /**
     * Prints a message to the console followed by a newline.
     *
     * @param message the message to be printed
     */
    public static void print(String message){
        System.out.println(message);
    }

    /**
     * Prints a message to the console without a newline.
     *
     * @param message the message to be printed
     */
    public static void _print(String message){
        System.out.print(message);
    }

    /**
     * Logs a message with the prefix "[ log ]".
     *
     * @param message the message to be logged
     */
    public static void log(String message){
        print("[ log ] : " + message);
    }

    /**
     * Prints a warning message with the prefix "[ warning ]".
     *
     * @param message the warning message to be printed
     */
    public static void warning(String message){
        print("[ warning ] : " + message);
    }

    /**
     * Prints an error message to the error console with the prefix "[ error ]".
     *
     * @param message the error message to be printed
     */
    public static void error(String message){
        System.err.println("[ error ] : " + message);
    }

    /**
     * Prints a line separator to the console.
     */
    public static void lineSeparator(){
        print("--------------------------------------------------\n");
    }
}

