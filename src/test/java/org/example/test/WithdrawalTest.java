package org.example.test;

import org.example.atm.ATM;
import org.example.atm.ConcurrencyWithdrawal;
import org.example.atm.Denomination;
import org.example.atm.Withdrawal;
import org.example.constant.ProjectConstants;
import org.example.exception.DenominationUnavailableException;
import org.example.exception.InsufficientFundsException;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

public class WithdrawalTest {

    /**
     * Tests a successful withdrawal operation from the ATM.
     * <p>
     * This test verifies that a withdrawal operation from the ATM is successful when the ATM has sufficient funds and
     * the required denominations to fulfill the withdrawal request. It initializes an ATM instance, sets up a withdrawal
     * operation with a specified amount, and executes the withdrawal. The test captures the output to the console and
     * asserts that the withdrawal success message is present in the output.
     *
     * @throws InsufficientFundsException       if the ATM does not have sufficient funds to fulfill the withdrawal
     * @throws DenominationUnavailableException if the required denominations for the withdrawal are unavailable in the ATM
     */
    @Test
    public void testSuccessfulWithdrawal() throws InsufficientFundsException, DenominationUnavailableException {
        ATM atm = new ATM();
        int amount = 700;
        Withdrawal withdrawal = new Withdrawal(amount, atm.getDenominations());

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        withdrawal.execute();
        String output = outContent.toString();
        assertTrue(output.contains(ProjectConstants.WITHDRAW_SUCCESS_MESSAGE));
    }

    /**
     * Tests an InsufficientFundsException thrown when the withdrawal amount exceeds the available funds in the ATM.
     * <p>
     * This test verifies that an InsufficientFundsException is thrown when attempting to withdraw an amount that exceeds
     * the available funds in the ATM. It initializes an ATM instance, sets up a withdrawal operation with an amount greater
     * than the total funds in the ATM, and expects an InsufficientFundsException to be thrown during the withdrawal
     * execution.
     *
     * @throws InsufficientFundsException       expected to be thrown when the withdrawal amount exceeds the available funds
     * @throws DenominationUnavailableException if the required denominations for the withdrawal are unavailable in the ATM
     */
    @Test(expected = InsufficientFundsException.class)
    public void testInsufficientException() throws InsufficientFundsException, DenominationUnavailableException {
        ATM atm = new ATM();
        int amount = 15000000;
        Withdrawal withdrawal = new Withdrawal(amount, atm.getDenominations());
        withdrawal.execute();
    }

    /**
     * Tests a DenominationUnavailableException thrown when a suitable combination of denominations is unavailable.
     * <p>
     * This test verifies that a DenominationUnavailableException is thrown when attempting to withdraw an amount that
     * cannot be dispensed due to an unavailable combination of denominations in the ATM. It initializes an ATM instance,
     * sets up a withdrawal operation with an amount that cannot be dispensed with the available denominations, and expects
     * a DenominationUnavailableException to be thrown during the withdrawal execution.
     *
     * @throws InsufficientFundsException       if the ATM does not have enough funds to fulfill the withdrawal
     * @throws DenominationUnavailableException expected to be thrown when a suitable combination of denominations is unavailable
     */
    @Test(expected = DenominationUnavailableException.class)
    public void testUnavailableCombination() throws InsufficientFundsException, DenominationUnavailableException {
        ATM atm = new ATM();
        int amount = 250;
        Withdrawal withdrawal = new Withdrawal(amount, atm.getDenominations());
        withdrawal.execute();
    }

    /**
     * Tests an InsufficientFundsException thrown when the ATM does not have enough funds to fulfill the withdrawal.
     * <p>
     * This test verifies that an InsufficientFundsException is thrown when attempting to withdraw an amount that exceeds
     * the available funds in the ATM. It initializes an ATM instance with specific denomination counts, sets up a withdrawal
     * operation with an amount that cannot be fulfilled due to insufficient funds in the ATM, and expects an
     * InsufficientFundsException to be thrown during the withdrawal execution.
     *
     * @throws InsufficientFundsException       expected to be thrown when the ATM does not have enough funds to fulfill the withdrawal
     * @throws DenominationUnavailableException if the required denominations for the withdrawal are unavailable in the ATM
     */
    @Test(expected = InsufficientFundsException.class)
    public void testInsufficientFundsException() throws InsufficientFundsException, DenominationUnavailableException {
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
     * It asserts that the denomination counts of the ATM are non-negative after the concurrent withdrawals.
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
        @Test
        public void testConcurrentWithdrawals() throws InterruptedException {
            final int numberOfThreads = 2;
            final int amountPerThread = 500;

            ATM atm = new ATM();
            printDenominationCounts(atm);

            List<Thread> threads = new ArrayList<>();
            CountDownLatch latch = new CountDownLatch(numberOfThreads);

            IntStream.range(0, numberOfThreads)
                    .mapToObj(i -> new Thread(() -> {
                        try {
                            ConcurrencyWithdrawal concurrencyWithdrawal = new ConcurrencyWithdrawal(atm, amountPerThread);
                            concurrencyWithdrawal.execute();
                        }
                        finally {
                            latch.countDown();
                        }
                    }))
                    .forEach(thread -> {
                        threads.add(thread);
                        thread.start();
                    });

            latch.await();


            printDenominationCounts(atm);

            // Assert the final state of the ATM after concurrent withdrawals
            assertTrue(atm.getDenominations().get(Denomination.HUNDRED) >= 0);
            assertTrue(atm.getDenominations().get(Denomination.TWO_HUNDRED) >= 0);
            assertTrue(atm.getDenominations().get(Denomination.FIVE_HUNDRED) >= 0);
        }

    /**
     * Prints the current denomination counts of the ATM.
     * <p>
     * This method retrieves the current denomination counts from the specified ATM instance and prints them to the console.
     * It iterates through the denominations and their corresponding counts in the ATM, displaying each denomination
     * followed by its count. Finally, it prints an empty line for readability.
     *
     * @param atm the ATM instance from which to retrieve the denomination counts
     */
        private void printDenominationCounts(ATM atm) {
            ConcurrentHashMap<Denomination, Integer> denominations = atm.getDenominations();
            System.out.println("Denomination Counts");
            for (Map.Entry<Denomination, Integer> entry : denominations.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            System.out.println();
        }
    }




