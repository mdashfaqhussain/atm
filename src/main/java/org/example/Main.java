package org.example;

import org.example.atm.ATM;
import org.example.atm.Withdrawal;
import org.example.exception.AmountNegativeException;
import org.example.exception.DenominationUnavailableException;
import org.example.exception.InsufficientFundsException;

import java.util.Scanner;

/**
 * The Main class serves as the entry point for the ATM application.
 * It initializes the ATM object and starts a loop to continuously prompt the user for input.
 * Users can choose to withdraw funds or exit the application.
 */
public class Main {

    /**
     * The main method serves as the entry point of the ATM application.
     * It initializes the ATM object and starts a loop to continuously prompt the user for input.
     * Users can choose to withdraw funds or exit the application.
     *
     * @param args The command-line arguments (not used in this application).
     */
    public static void main(String[] args) throws DenominationUnavailableException, InsufficientFundsException, AmountNegativeException {
        ATM atm = new ATM();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Withdraw");
            System.out.println("2. Exit");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    int withdrawAmount = getPositiveInput(scanner, "Enter amount to withdraw (positive integer):");
                    Withdrawal withdrawal = new Withdrawal(withdrawAmount, atm.getDenominations());
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

    // Helper method to get positive input from the user
    private static int getPositiveInput(Scanner scanner, String prompt) {
        int input;
        do {
            System.out.println(prompt);
            while (!scanner.hasNextInt()) {
                System.out.println("Please enter a valid positive integer:");
                scanner.next();
            }
            input = scanner.nextInt();
        } while (input <= 0);
        return input;
    }

}
