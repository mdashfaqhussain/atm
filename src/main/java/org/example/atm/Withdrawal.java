package org.example.atm;

import org.example.constant.ProjectConstants;
import org.example.exception.DenominationUnavailableException;
import org.example.exception.InsufficientFundsException;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Withdrawal {
    private final int amount;
    private final HashMap<Denomination, Integer> denominations;
    private final ReentrantLock lock;

    public Withdrawal(int amount, HashMap<Denomination, Integer> denominations, ReentrantLock lock) {
        this.amount = amount;
        this.denominations = denominations;
        this.lock = lock;
    }

    /**
     * Executes the withdrawal process.
     * This method attempts to withdraw the specified amount from the ATM. It first checks if the
     * total balance in the ATM is sufficient to cover the requested amount. Then, it tries to
     * dispense the amount using the highest denominations available to minimize the number of notes.
     * If the ATM does not have enough balance or the exact amount cannot be dispensed due to
     * unavailable denominations, it throws the appropriate exception.
     *
     * @throws InsufficientFundsException if the ATM balance is insufficient for the withdrawal amount.
     * @throws DenominationUnavailableException if the ATM cannot dispense the exact amount with available denominations.
     */
    public void execute() throws InsufficientFundsException, DenominationUnavailableException {
        lock.lock();
        try {

            int totalBalance = 0;
            for (Map.Entry<Denomination, Integer> entry : denominations.entrySet()) {
                totalBalance += entry.getKey().getValue() * entry.getValue();
            }

            if (amount > totalBalance) {
                throw new InsufficientFundsException(ProjectConstants.INSUFFICIENT_FUNDS_MESSAGE);
            }

            int remainingAmount = amount;
            Map<Denomination, Integer> dispensedNotes = new HashMap<>();

            List<Denomination> sortedDenominations = Arrays.asList(Denomination.values());
            sortedDenominations.sort((d1, d2) -> d2.getValue() - d1.getValue());

            for (Denomination denomination : sortedDenominations) {
                int quantity = denominations.getOrDefault(denomination, 0);
                if (quantity <= 0 || denomination.getValue() > remainingAmount) {
                    continue;
                }
                int notesToDispense = Math.min(remainingAmount / denomination.getValue(), quantity);
                dispensedNotes.put(denomination, notesToDispense);
                remainingAmount -= notesToDispense * denomination.getValue();
                if (remainingAmount == 0) {
                    break;
                }
            }
            if (remainingAmount > 0) {
                throw new DenominationUnavailableException(ProjectConstants.DENOMINATION_UNAVAILABLE_MESSAGE);
            }
            for (Map.Entry<Denomination, Integer> entry : dispensedNotes.entrySet()) {
                System.out.println("Dispensing " + entry.getValue() + " x " + entry.getKey().getValue());
            }
            System.out.println(ProjectConstants.WITHDRAW_SUCCESS_MESSAGE);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks if the ATM can dispense the specified amount with the available denominations.
     * This method iterates over the available denominations to simulate dispensing the requested amount.
     * It calculates whether the exact amount can be dispensed given the current inventory of denominations
     * without actually modifying the inventory.
     *
     * @param amount the amount of money to check for dispensing
     * @return true if the ATM can dispense the exact amount with the available denominations, false otherwise
     * @throws InsufficientFundsException if the total balance in the ATM is insufficient for the requested amount
     */

    private boolean canDispenseAmount(int amount) throws InsufficientFundsException {
        int remainingAmount = amount;
        for (Denomination denomination : Denomination.values()) {
            int quantity = denominations.getOrDefault(denomination, 0);
            int count = remainingAmount / denomination.getValue();
            if (count > 0) {
                int notesToDispense = Math.min(count, quantity);
                remainingAmount -= notesToDispense * denomination.getValue();
            }
        }
        return remainingAmount == 0;
    }
}
