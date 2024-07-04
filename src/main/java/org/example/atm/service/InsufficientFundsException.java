package org.example.atm.service;

public class InsufficientFundsException extends Throwable {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
