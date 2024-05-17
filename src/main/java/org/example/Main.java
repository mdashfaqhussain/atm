package org.example;

import org.example.atm.ATM;
import org.example.atm.ConcurrencyWithdrawal;

import java.util.Scanner;

public class Main {

    /**
     * The main method serves as the entry point of the ATM application.
     * It initializes the ATM object and starts a loop to continuously prompt the user for input.
     * Users can choose to withdraw funds or exit the application.
     *
     * @param args The command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        ATM atm = new ATM();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Withdraw");
            System.out.println("2. Exit");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    int withdrawAmount = getPositiveInput(scanner, "Enter amount to withdraw (positive integer):");
                    ConcurrencyWithdrawal withdrawal = new ConcurrencyWithdrawal(atm, withdrawAmount);
                    withdrawal.execute();
                    break;
                case 2:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    /**
     * Prompts the user for a positive integer input using the provided scanner.
     *
     * @param scanner the Scanner object to read input from
     * @param message the message to display prompting the user for input
     * @return the positive integer input provided by the user
     */
    private static int getPositiveInput(Scanner scanner, String message) {
        int input = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.println(message);
            input = scanner.nextInt();
            if (input > 0) {
                validInput = true;
            } else {
                System.out.println("Invalid amount. Please enter a positive integer.");
            }
        }
        return input;
    }

}
