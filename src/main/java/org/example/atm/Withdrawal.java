package org.example.atm;

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

    public void execute() throws InsufficientFundsException, DenominationUnavailableException {
        lock.lock();
        try {
            if (!canDispenseAmount(amount)) {
                throw new DenominationUnavailableException("ATM cannot dispense the exact amount with available denominations.");
            }
            int remainingAmount = amount;
            Map<Denomination, Integer> dispensedNotes = new HashMap<>();

            // Sort denominations in descending order (highest value first)
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
                throw new DenominationUnavailableException("ATM cannot dispense the exact amount with available denominations.");
            }
            for (Map.Entry<Denomination, Integer> entry : dispensedNotes.entrySet()) {
                System.out.println("Dispensing " + entry.getValue() + " x " + entry.getKey().getValue());
            }
            System.out.println("Withdrawal successful.");
        } finally {
            lock.unlock();
        }
    }


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
