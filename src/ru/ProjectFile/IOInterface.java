package ru.ProjectFile;

import java.util.Scanner;

public class IOInterface {
    private static Scanner inputInterface = new Scanner(System.in);

    public static String receiveMess(){
        return inputInterface.nextLine();
    }

    public static void transmitMess(Object obj){
        System.out.println(obj.toString());
    }

}
