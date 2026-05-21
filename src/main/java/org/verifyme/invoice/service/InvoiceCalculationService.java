package org.verifyme.invoice.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.verifyme.invoice.dto.InvoiceRequest;
import org.verifyme.invoice.util.CurrencyRoundingUtil;
import org.verifyme.invoice.validator.InvoiceRequestValidator;

import java.math.BigDecimal;

@ApplicationScoped
public class InvoiceCalculationService {

    @Inject
    InvoiceRequestValidator validator;

    @Inject
    CurrencyConversionService conversionService;

    public String calculateInvoiceTotal(
            InvoiceRequest request
    ) {

        validator.validate(request);

        String baseCurrency =
                request.getInvoice().getCurrency();

        String date =
                request.getInvoice().getDate();

        BigDecimal total = BigDecimal.ZERO;

        for (var line : request.getInvoice().getLines()) {

            BigDecimal convertedAmount =
                    conversionService.convert(
                            date,
                            line.getCurrency(),
                            baseCurrency,
                            line.getAmount()
                    );

            total = total.add(convertedAmount);
        }

        return CurrencyRoundingUtil
                .roundAmount(total)
                .toPlainString();
    }
}