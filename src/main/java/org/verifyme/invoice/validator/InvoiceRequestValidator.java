package org.verifyme.invoice.validator;

import jakarta.enterprise.context.ApplicationScoped;

import org.verifyme.invoice.dto.InvoiceRequest;
import org.verifyme.invoice.exception.InvoiceValidationException;

import java.math.BigDecimal;

@ApplicationScoped
public class InvoiceRequestValidator {

    public void validate(InvoiceRequest request) {

        if (request == null || request.getInvoice() == null) {
             throw new InvoiceValidationException("Invalid invoice  body");
        }

        var invoice = request.getInvoice();

        if (invoice.getCurrency() == null ||
                invoice.getCurrency().isBlank()) {

            throw new InvoiceValidationException(
                    "Invoice currency is required"
            );
        }

        if (invoice.getDate() == null ||
                invoice.getDate().isBlank()) {

            throw new IllegalArgumentException(
                    "Invoice date is required"
            );
        }

        if (invoice.getLines() == null ||
                invoice.getLines().isEmpty()) {

            throw new IllegalArgumentException(
                    "Invoice lines are required"
            );
        }

        invoice.getLines().forEach(line -> {

            if (line.getCurrency() == null ||
                    line.getCurrency().isBlank()) {

                throw new IllegalArgumentException(
                        "Line currency is required"
                );
            }

            if (line.getAmount() == null ||
                    line.getAmount()
                            .compareTo(BigDecimal.ZERO) <= 0) {

                throw new IllegalArgumentException(
                        "Line amount must be positive"
                );
            }
        });
    }
}