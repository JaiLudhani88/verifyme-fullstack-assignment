package org.verifyme.invoice.exception;

public class InvoiceValidationException
        extends RuntimeException {

    public InvoiceValidationException(String message) {
        super(message);
    }
}