package org.example.atm;

import java.util.Scanner;

/**
 * Represents a utility class for reading integer input from the console.
 * <p>
 * The ConsoleInput class provides functionality for reading integer input from the console using the standard input stream (System.in).
 * It initializes a Scanner object to facilitate reading input from the console and provides a method to read integers.
 */
public class ConsoleInput {
    private final Scanner scanner;

    public ConsoleInput() {
        scanner = new Scanner(System.in);
    }

    public int readInt() {
        return scanner.nextInt();
    }
}
