package org.verifyme.invoice.exception;

public class CurrencyConversionException
        extends RuntimeException {

    public CurrencyConversionException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}