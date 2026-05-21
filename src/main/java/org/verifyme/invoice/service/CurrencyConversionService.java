package org.verifyme.invoice.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.verifyme.invoice.exception.InvoiceValidationException;
import org.verifyme.invoice.util.CurrencyRoundingUtil;

import java.math.BigDecimal;

@ApplicationScoped
public class CurrencyConversionService {

    @Inject
    ExchangeRateService exchangeRateService;

    public BigDecimal convert(
            String date,
            String fromCurrency,
            String toCurrency,
            BigDecimal amount
    ) {

        validateInput(
                date,
                fromCurrency,
                toCurrency,
                amount
        );

        // no conversion required
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {

            return CurrencyRoundingUtil
                    .roundAmount(amount);
        }

        BigDecimal exchangeRate =
                exchangeRateService.getExchangeRate(
                        date,
                        fromCurrency,
                        toCurrency
                );

        BigDecimal convertedAmount =
                amount.multiply(exchangeRate);

        // assignment requires line total rounded to 2 decimals
        return CurrencyRoundingUtil
                .roundAmount(convertedAmount);
    }

    private void validateInput(
            String date,
            String fromCurrency,
            String toCurrency,
            BigDecimal amount
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

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvoiceValidationException(
                    "Amount must be positive"
            );
        }
    }
}