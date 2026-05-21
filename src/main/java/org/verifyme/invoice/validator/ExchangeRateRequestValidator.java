package org.verifyme.invoice.validator;

import jakarta.enterprise.context.ApplicationScoped;

import org.verifyme.invoice.exception.InvoiceValidationException;

@ApplicationScoped
public class ExchangeRateRequestValidator {

    public void validate(
            String date,
            String fromCurrency,
            String toCurrency
    ) {

        if (date == null || date.isBlank()) {

            throw new InvoiceValidationException(
                    "Invoice date is required"
            );
        }

        if (fromCurrency == null ||
                fromCurrency.isBlank()) {

            throw new InvoiceValidationException(
                    "Source currency is required"
            );
        }

        if (toCurrency == null ||
                toCurrency.isBlank()) {

            throw new InvoiceValidationException(
                    "Target currency is required"
            );
        }
    }
}