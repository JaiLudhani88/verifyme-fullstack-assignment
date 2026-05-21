package org.verifyme.invoice.service;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.verifyme.invoice.client.FrankfurterClient;
import org.verifyme.invoice.dto.ExchangeRateResponse;
import org.verifyme.invoice.exception.CurrencyConversionException;
import org.verifyme.invoice.exception.ExchangeRateNotFoundException;
import org.verifyme.invoice.util.CurrencyRoundingUtil;
import org.verifyme.invoice.validator.ExchangeRateRequestValidator;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class ExchangeRateService {

    @Inject
    @RestClient
    FrankfurterClient frankfurterClient;

    @Inject
    ExchangeRateRequestValidator validator;

    public BigDecimal getExchangeRate(
            String date,
            String fromCurrency,
            String toCurrency
    ) {

        validator.validate(
                date,
                fromCurrency,
                toCurrency
        );

        try {

            List<ExchangeRateResponse> response =
                    frankfurterClient.getExchangeRates(
                            date,
                            fromCurrency,
                            toCurrency
                    );

            if (response == null || response.isEmpty()) {

                throw new ExchangeRateNotFoundException(
                        "Exchange rate not found for "
                                + fromCurrency
                                + " to "
                                + toCurrency
                                + " on "
                                + date
                );
            }

            ExchangeRateResponse exchangeRate =
                    response.get(0);

            if (exchangeRate.getRate() == null) {

                throw new ExchangeRateNotFoundException(
                        "Exchange rate is missing"
                );
            }

            return CurrencyRoundingUtil.roundRate(
                    exchangeRate.getRate()
            );

        } catch (ExchangeRateNotFoundException ex) {

            throw ex;

        } catch (Exception ex) {

            throw new CurrencyConversionException(
                    "Failed to fetch exchange rate",
                    ex
            );
        }
    }
}