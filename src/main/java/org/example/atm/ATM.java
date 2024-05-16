package org.example.atm;

import org.example.exception.DenominationUnavailableException;
import org.example.exception.InsufficientFundsException;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ATM {
    private HashMap<Denomination, Integer> denominations;
    private final ReentrantLock lock;

    public ATM() {
        this.denominations = new HashMap<>();
        this.denominations.put(Denomination.HUNDRED, 10);
        this.denominations.put(Denomination.TWO_HUNDRED, 5);
        this.denominations.put(Denomination.FIVE_HUNDRED, 2);
        this.lock = new ReentrantLock();
    }

    public void withdraw(int amount) throws InsufficientFundsException, DenominationUnavailableException {
        Withdrawal withdrawal = new Withdrawal(amount, denominations, lock);
        withdrawal.execute();
    }

    public ReentrantLock getLock() {
        return lock;
    }
}
