package org.verifyme.invoice.exception;

public class ExchangeRateNotFoundException
        extends RuntimeException {

    public ExchangeRateNotFoundException(String message) {
        super(message);
    }
}