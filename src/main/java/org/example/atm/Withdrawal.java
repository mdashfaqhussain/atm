package org.example.atm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.constant.ProjectConstants;
import org.example.exception.AmountNegativeException;
import org.example.exception.DenominationUnavailableException;
import org.example.exception.InsufficientFundsException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


/**
 * The Withdrawal class handles the execution of withdrawal operations from an ATM.
 * It ensures thread safety, checks for sufficient funds, dispenses the correct denominations,
 * updates the available denominations, and logs the dispensed notes.
 */

public class Withdrawal {

    private static final Logger logger = LogManager.getLogger(Withdrawal.class);
    private final int amount;
    private final ConcurrentHashMap<Denomination, Integer> denominations;

    /**
     * Constructs a Withdrawal object with the specified amount and denominations.
     *
     * @param amount        The amount to withdraw.
     * @param denominations The denominations available in the ATM.
     */
    public Withdrawal(int amount, ConcurrentHashMap<Denomination, Integer> denominations) {
        this.amount = amount;
        this.denominations = denominations;
    }

    /**
     * Executes the withdrawal process.
     *
     * @throws InsufficientFundsException     If the ATM has insufficient funds.
     * @throws DenominationUnavailableException If a required denomination is not available.
     * @throws AmountNegativeException        If the withdrawal amount is negative.
     */
    public void execute() throws InsufficientFundsException, DenominationUnavailableException, AmountNegativeException {

            validateAmount();
            checkSufficientBalance();
            Map<Denomination, Integer> dispensedNotes = dispenseNotes();
            updateDenominations(dispensedNotes);
            handleRemainingAmount(dispensedNotes);
            printDispensedNotes(dispensedNotes);
            System.out.println(ProjectConstants.WITHDRAW_SUCCESS_MESSAGE);

    }

    /**
     * Validates that the withdrawal amount is positive.
     *
     * @throws AmountNegativeException If the withdrawal amount is not positive.
     */
    private void validateAmount() throws AmountNegativeException {
        if (amount <= 0) {
            throw new AmountNegativeException(ProjectConstants.AMOUNT_POSITIVE_MESSAGE);
        }
    }

    /**
     * Checks if there are sufficient funds in the ATM for the withdrawal.
     *
     * @throws InsufficientFundsException If the ATM has insufficient funds.
     */
    private void checkSufficientBalance() throws InsufficientFundsException {
        int totalBalance = calculateTotalBalance();
        if (amount > totalBalance) {
            throw new InsufficientFundsException(ProjectConstants.INSUFFICIENT_FUNDS_MESSAGE);
        }
    }

    /**
     * Calculates the total balance of the ATM based on available denominations.
     *
     * @return The total balance of the ATM.
     */
    private int calculateTotalBalance() {
        return denominations.entrySet().stream()
                .mapToInt(entry -> entry.getKey().getValue() * entry.getValue())
                .sum();
    }

    /**
     * Dispenses notes based on the withdrawal amount and available denominations.
     *
     * @return A map containing the dispensed notes.
     */
    private Map<Denomination, Integer> dispenseNotes() {
        Map<Denomination, Integer> dispensedNotes = new HashMap<>();
        List<Denomination> sortedDenominations = Arrays.stream(Denomination.values())
                .sorted(Comparator.comparingInt(Denomination::getValue).reversed())
                .collect(Collectors.toList());

        int remainingAmountToDispense = amount;
        for (Denomination denomination : sortedDenominations) {
            int quantity = denominations.getOrDefault(denomination, 0);
            if (quantity <= 0 || denomination.getValue() > remainingAmountToDispense) {
                continue;
            }
            int notesToDispense = Math.min(remainingAmountToDispense / denomination.getValue(), quantity);
            dispensedNotes.put(denomination, notesToDispense);
            remainingAmountToDispense -= notesToDispense * denomination.getValue();
            if (remainingAmountToDispense == 0) {
                break;
            }
        }
        return dispensedNotes;
    }

    /**
     * Updates the ATM denominations after dispensing notes.
     *
     * @param dispensedNotes The dispensed notes to update denominations.
     */
    private void updateDenominations(Map<Denomination, Integer> dispensedNotes) {
        dispensedNotes.forEach((denomination, count) -> {
            int currentQuantity = denominations.getOrDefault(denomination, 0);
            denominations.put(denomination, currentQuantity - count);
        });
    }

    /**
     * Handles any remaining amount after dispensing notes.
     *
     * @param dispensedNotes The dispensed notes.
     * @throws DenominationUnavailableException If a required denomination is not available.
     */
    private void handleRemainingAmount(Map<Denomination, Integer> dispensedNotes) throws DenominationUnavailableException {
        int remainingAmountToDispense = amount - dispensedNotes.entrySet().stream()
                .mapToInt(entry -> entry.getKey().getValue() * entry.getValue())
                .sum();
        if (remainingAmountToDispense > 0) {
            throw new DenominationUnavailableException(ProjectConstants.DENOMINATION_UNAVAILABLE_MESSAGE);
        }
    }

    /**
     * Prints the dispensed notes.
     *
     * @param dispensedNotes The dispensed notes to print.
     */
    private void printDispensedNotes(Map<Denomination, Integer> dispensedNotes) {
        dispensedNotes.forEach((denomination, count) ->
                System.out.println("Dispensing " + count + " x " + denomination.getValue()));
    }
}
