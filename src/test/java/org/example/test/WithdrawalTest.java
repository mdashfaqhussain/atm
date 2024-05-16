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
        // Set up an ATM with a low balance (less than the withdrawal amount)
        HashMap<Denomination, Integer> denominations = atm.getDenominations();
        denominations.put(Denomination.HUNDRED, 1); // Assume there is only one hundred in the ATM
        denominations.put(Denomination.TWO_HUNDRED, 0); // Assume there are no two hundreds in the ATM
        denominations.put(Denomination.FIVE_HUNDRED, 0); // Assume there are no five hundreds in the ATM

        int amount = 500; // Assuming the total balance is less than 500
        Withdrawal withdrawal = new Withdrawal(amount, denominations, atm.getLock());
        withdrawal.execute();
    }


}
