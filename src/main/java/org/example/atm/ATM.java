package org.example.atm;

import org.example.constant.ProjectConstants;
import org.example.exception.DenominationUnavailableException;
import org.example.exception.InsufficientFundsException;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ATM {


    private ConcurrentHashMap<Denomination, Integer> denominations;

    public ConcurrentHashMap<Denomination, Integer> getDenominations() {
        return denominations;
    }
    private final ReentrantLock lock;
    public ReentrantLock getLock() {
        return lock;
    }

    /**
     * Constructs an ATM with a predefined set of denominations and their quantities.
     * This no-argument constructor initializes the ATM with the following denominations:
     * - 10 notes of 100 units each
     * - 5 notes of 200 units each
     * - 2 notes of 500 units each
     * It also initializes a reentrant lock to handle concurrency for withdrawal operations.
     */
    public ATM() {
        this.denominations = new ConcurrentHashMap<>();
        initializeDenominations();
        this.lock = new ReentrantLock();
    }

    private void initializeDenominations(){
        this.denominations.put(Denomination.HUNDRED, ProjectConstants.INITIAL_HUNDRED_NOTES);
        this.denominations.put(Denomination.TWO_HUNDRED, ProjectConstants.INITIAL_TWO_HUNDRED_NOTES);
        this.denominations.put(Denomination.FIVE_HUNDRED, ProjectConstants.INITIAL_FIVE_HUNDRED_NOTES);
    }

    /**
     * Withdraws the specified amount from the ATM.
     * This method creates a Withdrawal object and executes the withdrawal
     * process. It checks if the ATM has sufficient funds and the necessary
     * denominations to dispense the requested amount.
     *
     * @param amount the amount of money to withdraw
     * @throws InsufficientFundsException if the ATM balance is insufficient for the withdrawal amount
     * @throws DenominationUnavailableException if the ATM cannot dispense the exact amount with available denominations
     */
    public void withdraw(int amount) throws InsufficientFundsException, DenominationUnavailableException {
        Withdrawal withdrawal = new Withdrawal(amount, denominations, lock);
        withdrawal.execute();
    }


}
