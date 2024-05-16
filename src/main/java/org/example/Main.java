package org.example;

import org.example.atm.ATM;
import org.example.atm.ConsoleInput;
import org.example.atm.ConcurrencyWithdrawal;

public class Main {
    public static void main(String[] args) {
        ATM atm = new ATM();
        ConsoleInput input = new ConsoleInput();

        while (true) {
            System.out.println("1. Withdraw");
            System.out.println("2. Exit");


            int choice = input.readInt();

            switch (choice) {
                case 1:
                    int withdrawAmount = 0;
                    while (withdrawAmount <= 0) {
                        System.out.println("Enter amount to withdraw (positive integer):");
                        withdrawAmount = input.readInt();
                        if (withdrawAmount <= 0) {
                            System.out.println("Invalid amount. Please enter a positive integer.");
                        }
                    }
//                    System.out.println("Enter amount to withdraw:");
//                    int withdrawAmount = input.readInt();
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
}
