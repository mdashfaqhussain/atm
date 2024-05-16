package org.example.test;

import org.example.atm.ATM;
import org.example.atm.Denomination;
import org.example.atm.Withdrawal;
import org.example.exception.DenominationUnavailableException;
import org.example.exception.InsufficientFundsException;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class WithdrawalTest {

    @Test
    public void testSuccessfulWithdrawal() throws InsufficientFundsException, DenominationUnavailableException {
        ATM atm = new ATM();
        int amount = 700;
        Withdrawal withdrawal = new Withdrawal(amount, atm.getDenominations(), atm.getLock());
        withdrawal.execute();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        withdrawal.execute();
        String output = outContent.toString();
        assertTrue(output.contains("Withdrawal successful."));
    }

    @Test(expected = DenominationUnavailableException.class)
    public void testInsufficientFunds() throws InsufficientFundsException, DenominationUnavailableException {
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
}
