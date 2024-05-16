package org.example.atm;

import org.example.exception.DenominationUnavailableException;
import org.example.exception.InsufficientFundsException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadedWithdrawal {
    private final ATM atm;
    private final int amount;
    private final ExecutorService executor;
    private final ReentrantLock lock;

    public MultiThreadedWithdrawal(ATM atm, int amount) {
        this.atm = atm;
        this.amount = amount;
        this.executor = Executors.newFixedThreadPool(10);
        this.lock = atm.getLock();
    }

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
