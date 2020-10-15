package server;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {
    private static LinkedList<String> files = new LinkedList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String inputString = null;
        String[] input;
        LinkedList<String> allowedFileNames = new LinkedList<>(Arrays.asList("file1", "file2", "file3", "file4", "file5", "file6", "file7", "file8", "file9", "file10"));
        inputString = scanner.nextLine();
        while (!"exit".equals(inputString)) {

            input = inputString.split(" ");
            if (!allowedFileNames.contains(input[1])) {
                System.out.println("Cannot " + input[0] + " the file " + input[1]);
            }
            else {
                switch (input[0]) {
                    case "add":
                        add(input[1]);
                        break;
                    case "get":
                        get(input[1]);
                        break;
                    case "delete":
                        delete(input[1]);
                        break;
                }
            }
            inputString = scanner.nextLine();
        }
    }

    private static void add(String name) {
        if (!files.contains(name)) {
            files.add(name);
            System.out.println("The file " + name + " added successfully");
        }
        else {
            System.out.println("Cannot add the file " + name);
        }

    }

    private static void get(String name) {
        if (files.contains(name)) {
            System.out.println("The file " + name + " was sent");
        }
        else {
            System.out.println("The file " + name + " not found");
        }
    }

    private static void delete(String name) {
        if (files.contains(name)) {
            files.remove(name);
            System.out.println("The file " + name + " was deleted");
        }
        else {
            System.out.println("The file " + name + " not found");
        }
    }
}