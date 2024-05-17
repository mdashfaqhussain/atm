package org.example.atm;

import org.example.constant.ProjectConstants;
import org.example.exception.DenominationUnavailableException;
import org.example.exception.InsufficientFundsException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Withdrawal {
    private final int amount;
    private final ConcurrentHashMap<Denomination, Integer> denominations;
    private final ReentrantLock lock;

    public Withdrawal(int amount, ConcurrentHashMap<Denomination, Integer> denominations) {
        this.amount = amount;
        this.denominations = denominations;
      this.lock = new ReentrantLock();
    }

    /**
     * Executes a withdrawal operation from the ATM.
     * <p>
     * This method calculates the total balance of the ATM by summing the values of each denomination multiplied by
     * their respective quantities. If the requested withdrawal amount exceeds the total balance, an InsufficientFundsException
     * is thrown.
     * <p>
     * The method then iterates through the denominations in descending order of value and attempts to dispense the
     * required amount using the available denominations. It maintains a map of dispensed notes and their quantities.
     * If a denomination is not available or its quantity is insufficient to fulfill the withdrawal, the method continues
     * to the next denomination.
     * <p>
     * After successfully dispensing the required amount, the method updates the ATM's denomination counts accordingly,
     * deducting the dispensed notes from their respective quantities. If the ATM does not have sufficient denominations
     * to fully dispense the requested amount, a DenominationUnavailableException is thrown.
     * <p>
     * Upon successful withdrawal, the method prints a message indicating the dispensed notes and a confirmation message.
     *
     * @throws InsufficientFundsException       if the ATM does not have sufficient funds to fulfill the withdrawal
     * @throws DenominationUnavailableException if the required denominations for the withdrawal are unavailable in the ATM
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
                denominations.put(denomination, quantity - notesToDispense);
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
