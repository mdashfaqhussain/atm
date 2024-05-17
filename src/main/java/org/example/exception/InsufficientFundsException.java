package org.example.exception;

/**
 * The InsufficientFundsException class represents an exception that is thrown when the ATM does not have
 * sufficient funds to fulfill a withdrawal request.
 */
public class InsufficientFundsException extends  Exception{
    public InsufficientFundsException(String message) {
        super(message);
    }
}
