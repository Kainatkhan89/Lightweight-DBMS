package com.dbms.org;

public class Utils {
    public static void print(String message){
        System.out.println(message);
    }
    
    public static void _print(String message){
        System.out.print(message);
    }

    public static void log(String message){
        print("[ log ] : " + message);
    }

    public static void error(String message){
        System.err.println("[ error ] : " + message);
    }

    public static void lineSeparator(){
        print("--------------------------------------------------\n");
    }
}
