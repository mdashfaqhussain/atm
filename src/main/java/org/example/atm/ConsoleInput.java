package org.example.atm;

import java.util.Scanner;

public class ConsoleInput {
    private Scanner scanner;

    public ConsoleInput() {
        scanner = new Scanner(System.in);
    }

    public int readInt() {
        return scanner.nextInt();
    }
}
