package org.example.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.atm.ATM;
import org.example.atm.Denomination;
import org.example.atm.Withdrawal;
import org.example.constant.ProjectConstants;
import org.example.exception.AmountNegativeException;
import org.example.exception.DenominationUnavailableException;
import org.example.exception.InsufficientFundsException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

/**
 * The WithdrawalTest class contains test cases to verify the functionality of the Withdrawal class.
 * It tests various scenarios such as successful withdrawals, insufficient funds, unavailable denominations, and concurrent withdrawals.
 */
public class WithdrawalTest {

    private static final Logger logger = LogManager.getLogger(WithdrawalTest.class);
    /**
     * Tests a successful withdrawal operation from the ATM.
     *
     * @throws InsufficientFundsException       if the ATM does not have sufficient funds to fulfill the withdrawal
     * @throws DenominationUnavailableException if the required denominations for the withdrawal are unavailable in the ATM
     */
    @Test
    public void testSuccessfulWithdrawal() throws InsufficientFundsException, DenominationUnavailableException, AmountNegativeException {
        ATM atm = new ATM();
        int amount = 700;
        Withdrawal withdrawal = new Withdrawal(amount, atm.getDenominations());

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        withdrawal.execute();
        String output = outContent.toString();
        assertTrue(output.contains(ProjectConstants.WITHDRAW_SUCCESS_MESSAGE));
        System.setOut(originalOut);
    }

    /**
     * Tests an InsufficientFundsException thrown when the withdrawal amount exceeds the available funds in the ATM.
     *
     * @throws InsufficientFundsException       expected to be thrown when the withdrawal amount exceeds the available funds
     * @throws DenominationUnavailableException if the required denominations for the withdrawal are unavailable in the ATM
     */
    @Test(expected = InsufficientFundsException.class)
    public void testInsufficientException() throws InsufficientFundsException, DenominationUnavailableException, AmountNegativeException {
        ATM atm = new ATM();
        int amount = 15000000;
        Withdrawal withdrawal = new Withdrawal(amount, atm.getDenominations());
        withdrawal.execute();
    }

    /**
     * Tests a DenominationUnavailableException thrown when a suitable combination of denominations is unavailable.
     *
     * @throws InsufficientFundsException       if the ATM does not have enough funds to fulfill the withdrawal
     * @throws DenominationUnavailableException expected to be thrown when a suitable combination of denominations is unavailable
     */
    @Test(expected = DenominationUnavailableException.class)
    public void testUnavailableCombination() throws InsufficientFundsException, DenominationUnavailableException, AmountNegativeException {
        ATM atm = new ATM();
        int amount = 250;
        Withdrawal withdrawal = new Withdrawal(amount, atm.getDenominations());
        withdrawal.execute();
    }

    /**
     * Tests an InsufficientFundsException thrown when the ATM does not have enough funds to fulfill the withdrawal.
     *
     * @throws InsufficientFundsException       expected to be thrown when the ATM does not have enough funds to fulfill the withdrawal
     * @throws DenominationUnavailableException if the required denominations for the withdrawal are unavailable in the ATM
     */
    @Test(expected = InsufficientFundsException.class)
    public void testInsufficientFundsException() throws InsufficientFundsException, DenominationUnavailableException, AmountNegativeException {
        ATM atm = new ATM();

        ConcurrentHashMap<Denomination, Integer> denominations = atm.getDenominations();
        denominations.put(Denomination.HUNDRED, 1);
        denominations.put(Denomination.TWO_HUNDRED, 0);
        denominations.put(Denomination.FIVE_HUNDRED, 0);

        int amount = 500;
        Withdrawal withdrawal = new Withdrawal(amount, denominations);
        withdrawal.execute();
    }


    /**
     * Tests concurrent withdrawals from the ATM.
     * <p>
     * This test verifies the correctness of concurrent withdrawals from the ATM. It initiates multiple withdrawal
     * threads, each attempting to withdraw a specified amount concurrently. The test ensures that the withdrawal
     * operations are executed safely and that the final state of the ATM after concurrent withdrawals is consistent.
     * It asserts that the denomination counts of the ATM are the exact number after all withdrawals.
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    @Test
    public void testConcurrentWithdrawals() throws InterruptedException {
        final int numberOfThreads = 3;
        final int[] amounts = {500, 700, 300};

        ATM atm = new ATM();
        int initialTotalBalance = calculateTotalBalance(atm.getDenominations());
        CountDownLatch latch = getCountDownLatch(numberOfThreads, amounts, atm);
        latch.await(); // Wait for all threads to finish
        int remainingTotalBalance = calculateTotalBalance(atm.getDenominations());
        // Assert the final state of the ATM after concurrent withdrawals
        assertEquals(initialTotalBalance - Arrays.stream(amounts).sum(), remainingTotalBalance);
    }



    /**
     * Creates a CountDownLatch and starts withdrawal threads based on the number of threads and withdrawal amounts provided.
     * <p>
     * This method creates a CountDownLatch with the specified number of threads. It then creates and starts withdrawal
     * threads, each attempting to withdraw a specified amount from the ATM. The threads decrement the latch count when
     * they finish their execution.
     *
     * @param numberOfThreads the number of withdrawal threads to create
     * @param amounts         an array containing withdrawal amounts for each thread
     * @param atm             the ATM object from which withdrawals are made
     * @return the CountDownLatch that synchronizes the test with the completion of all withdrawal threads
     */
    private static CountDownLatch getCountDownLatch(int numberOfThreads, int[] amounts, ATM atm) {
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            final int amountPerThread = amounts[i];
            Thread thread = new Thread(() -> {
                try {
                    Withdrawal withdrawal = new Withdrawal(amountPerThread, atm.getDenominations());
                    withdrawal.execute();
                } catch (DenominationUnavailableException | InsufficientFundsException | AmountNegativeException e) {
                    logger.error("Error occurred during withdrawal: " + e.getMessage());

                } finally {
                    latch.countDown();
                }
            });
            threads.add(thread);
            thread.start();
        }
        return latch;
    }


    /**
         * Calculates the total balance of the ATM based on the current denomination counts.
         *
         * @param denominations the denomination counts in the ATM
         * @return the total balance of the ATM
         */
    private int calculateTotalBalance(ConcurrentHashMap<Denomination, Integer> denominations) {
        return denominations.entrySet().stream()
                .mapToInt(entry -> entry.getKey().getValue() * entry.getValue())
                .sum();
    }
}







