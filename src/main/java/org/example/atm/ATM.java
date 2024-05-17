package org.example.atm;

import org.example.constant.ProjectConstants;
import org.example.exception.AmountNegativeException;
import org.example.exception.DenominationUnavailableException;
import org.example.exception.InsufficientFundsException;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;



/**
 * Represents an Automated Teller Machine (ATM) that manages the dispensing of money.
 * <p>
 * The ATM class provides functionality for withdrawing money from the ATM. It maintains the counts of different
 * denominations of currency available in the ATM and ensures thread-safe access to withdrawal operations.
 */
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
     *
     */
    public void withdraw(int amount)  {
        lock.lock();
        try {
            Withdrawal withdrawal = new Withdrawal(amount, denominations);
            withdrawal.execute();
        } catch (AmountNegativeException | InsufficientFundsException | DenominationUnavailableException e) {
            System.out.println(e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}
