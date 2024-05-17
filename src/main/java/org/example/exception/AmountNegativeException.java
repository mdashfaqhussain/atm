package org.example.exception;


/**
 * The DenominationUnavailableException class represents an exception that is thrown when the required denominations
 * for a withdrawal request are unavailable in the ATM.
 */
public class AmountNegativeException extends  Exception{
    public AmountNegativeException(String message) {
        super(message);
    }
}
