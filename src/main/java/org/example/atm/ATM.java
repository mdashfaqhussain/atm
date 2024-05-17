package org.example.atm;

import org.example.constant.ProjectConstants;
import org.example.exception.DenominationUnavailableException;
import org.example.exception.InsufficientFundsException;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ATM {
    private final ConcurrentHashMap<Denomination, Integer> denominations;
    private final ReentrantLock lock;

    public ATM() {
        this.denominations = new ConcurrentHashMap<>();
        initializeDenominations();
        this.lock = new ReentrantLock();
    }

    private void initializeDenominations() {
        this.denominations.put(Denomination.HUNDRED, ProjectConstants.INITIAL_HUNDRED_NOTES);
        this.denominations.put(Denomination.TWO_HUNDRED, ProjectConstants.INITIAL_TWO_HUNDRED_NOTES);
        this.denominations.put(Denomination.FIVE_HUNDRED, ProjectConstants.INITIAL_FIVE_HUNDRED_NOTES);
    }

    public ConcurrentHashMap<Denomination, Integer> getDenominations() {
        return denominations;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    /**
     * Withdraws the specified amount from the ATM.
     *
     * @param amount the amount to withdraw from the ATM
     * @throws InsufficientFundsException      if the ATM does not have enough funds to fulfill the withdrawal
     * @throws DenominationUnavailableException if the required denominations for the withdrawal are unavailable in the ATM
     */
    public void withdraw(int amount) throws InsufficientFundsException, DenominationUnavailableException {
        lock.lock();
        try {
            Withdrawal withdrawal = new Withdrawal(amount, denominations);
            withdrawal.execute();
        } finally {
            lock.unlock();
        }
    }
}
