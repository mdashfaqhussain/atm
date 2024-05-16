package org.example.atm;

import org.example.exception.DenominationUnavailableException;
import org.example.exception.InsufficientFundsException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrencyWithdrawal {
    private final ATM atm;
    private final int amount;
    private final ExecutorService executor;
    private final ReentrantLock lock;

    public ConcurrencyWithdrawal(ATM atm, int amount) {
        this.atm = atm;
        this.amount = amount;
        this.executor = Executors.newFixedThreadPool(10);
        this.lock = atm.getLock();
    }

    /**
     * Submits a withdrawal task to the executor service for asynchronous execution.
     * This method attempts to withdraw the specified amount from the ATM in a separate thread.
     * If the withdrawal fails due to insufficient funds or unavailable denominations,
     * it catches the exception and prints the error message.
     */
    public void execute() {
        executor.submit(() -> {
            try {
                atm.withdraw(amount);
            } catch (InsufficientFundsException | DenominationUnavailableException e) {
                System.out.println(e.getMessage());
            }
        });
    }
}
