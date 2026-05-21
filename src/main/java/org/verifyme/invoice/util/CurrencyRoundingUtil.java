package org.verifyme.invoice.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CurrencyRoundingUtil {

    private CurrencyRoundingUtil() {
    }

    public static BigDecimal roundRate(BigDecimal rate) {
        return rate.setScale(4, RoundingMode.HALF_UP);
    }

    public static BigDecimal roundAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}