package org.example.atm;

import org.example.constant.ProjectConstants;
import org.example.exception.DenominationUnavailableException;
import org.example.exception.InsufficientFundsException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrencyWithdrawal {
    private final ATM atm;
    private final int amount;

    public ConcurrencyWithdrawal(ATM atm, int amount) {
        this.atm = atm;
        this.amount = amount;
    }

    /**
     * Executes a withdrawal operation from the ATM.
     * <p>
     * This method initiates a withdrawal operation from the ATM by invoking the `withdraw` method on the ATM instance.
     * It catches InsufficientFundsException and DenominationUnavailableException if they occur during the withdrawal
     * process and prints their error messages to the console.
     * <p>
     * Note: This method is responsible for handling exceptions related to insufficient funds or unavailable denominations
     * during the withdrawal process and communicates these errors to the user by printing their error messages to the console.
     */
    public void execute() {
        try {
            atm.withdraw(amount);
        } catch (InsufficientFundsException | DenominationUnavailableException e) {
            System.out.println(e.getMessage());
        }
    }
}
