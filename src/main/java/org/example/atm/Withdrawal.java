package org.example.atm;

import org.example.constant.ProjectConstants;
import org.example.exception.DenominationUnavailableException;
import org.example.exception.InsufficientFundsException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Withdrawal {
    private final int amount;
    private final ConcurrentHashMap<Denomination, Integer> denominations;
    private final ReentrantLock lock;

    public Withdrawal(int amount, ConcurrentHashMap<Denomination, Integer> denominations, ReentrantLock lock) {
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
     * @throws InsufficientFundsException        if the ATM balance is insufficient for the withdrawal amount.
     * @throws DenominationUnavailableException  if the ATM cannot dispense the exact amount with available denominations.
     */
    public void execute() throws InsufficientFundsException, DenominationUnavailableException {
        lock.lock();
        try {
            int totalBalance = denominations.entrySet().stream()
                    .mapToInt(entry -> entry.getKey().getValue() * entry.getValue())
                    .sum();

            if (amount > totalBalance) {
                throw new InsufficientFundsException(ProjectConstants.INSUFFICIENT_FUNDS_MESSAGE);
            }

            int remainingAmount = amount;
            Map<Denomination, Integer> dispensedNotes = new HashMap<>();

            List<Denomination> sortedDenominations = Arrays.stream(Denomination.values())
                    .sorted(Comparator.comparingInt(Denomination::getValue).reversed())
                    .collect(Collectors.toList());

            for (Denomination denomination : sortedDenominations) {
                Optional<Integer> quantityOpt = Optional.ofNullable(denominations.get(denomination));
                if (quantityOpt.isEmpty() || quantityOpt.get() <= 0 || denomination.getValue() > remainingAmount) {
                    continue;
                }
                int quantity = quantityOpt.get();
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
            dispensedNotes.forEach((denomination, count) ->
                    System.out.println("Dispensing " + count + " x " + denomination.getValue()));
            System.out.println(ProjectConstants.WITHDRAW_SUCCESS_MESSAGE);
        } finally {
            lock.unlock();
        }
    }
}
