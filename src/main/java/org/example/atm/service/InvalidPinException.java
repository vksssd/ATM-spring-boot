package org.example.atm.service;

public class InvalidPinException extends Throwable {
    public InvalidPinException(String message) {
        super(message);
    }
}
