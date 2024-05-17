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
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

public class WithdrawalTest {

    @Test
    public void testSuccessfulWithdrawal() throws InsufficientFundsException, DenominationUnavailableException {
        ATM atm = new ATM();
        int amount = 700;
        Withdrawal withdrawal = new Withdrawal(amount, atm.getDenominations(), atm.getLock());

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        withdrawal.execute();
        String output = outContent.toString();
        assertTrue(output.contains(ProjectConstants.WITHDRAW_SUCCESS_MESSAGE));
    }

    @Test(expected = InsufficientFundsException.class)
    public void testDenominationUnavailableException() throws InsufficientFundsException, DenominationUnavailableException {
        ATM atm = new ATM();
        int amount = 15000000;
        Withdrawal withdrawal = new Withdrawal(amount, atm.getDenominations(), atm.getLock());
        withdrawal.execute();
    }

    @Test(expected = DenominationUnavailableException.class)
    public void testUnavailableCombination() throws InsufficientFundsException, DenominationUnavailableException {
        ATM atm = new ATM();
        int amount = 250;
        Withdrawal withdrawal = new Withdrawal(amount, atm.getDenominations(), atm.getLock());
        withdrawal.execute();
    }

    @Test(expected = InsufficientFundsException.class)
    public void testInsufficientFundsException() throws InsufficientFundsException, DenominationUnavailableException {
        ATM atm = new ATM();

        ConcurrentHashMap<Denomination, Integer> denominations = atm.getDenominations();
        denominations.put(Denomination.HUNDRED, 1);
        denominations.put(Denomination.TWO_HUNDRED, 0);
        denominations.put(Denomination.FIVE_HUNDRED, 0);

        int amount = 500;
        Withdrawal withdrawal = new Withdrawal(amount, denominations, atm.getLock());
        withdrawal.execute();
    }

    @Test
    public void testConcurrentWithdrawals() throws InterruptedException, InsufficientFundsException, DenominationUnavailableException {
        final int numberOfThreads = 5;
        final int amountPerThread = 100;

        ATM atm = new ATM();
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        IntStream.range(0, numberOfThreads)
                .mapToObj(i -> new Thread(() -> {
                    try {
                        ConcurrencyWithdrawal concurrencyWithdrawal = new ConcurrencyWithdrawal(atm, amountPerThread);
                        concurrencyWithdrawal.execute();
                    } finally {
                        latch.countDown();
                    }
                }))
                .forEach(Thread::start);
        //waiting for other threads to complete
        latch.await();

        // Assert the final state of the ATM after concurrent withdrawals
        assertTrue(atm.getDenominations().get(Denomination.HUNDRED) >= 0);
        assertTrue(atm.getDenominations().get(Denomination.TWO_HUNDRED) >= 0);
        assertTrue(atm.getDenominations().get(Denomination.FIVE_HUNDRED) >= 0);
    }


}
