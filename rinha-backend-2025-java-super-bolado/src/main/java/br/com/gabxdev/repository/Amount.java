package br.com.gabxdev.repository;

import br.com.gabxdev.mapper.PaymentRequestParse;

import java.math.BigDecimal;

public class Amount {
    private static BigDecimal amount = BigDecimal.ZERO;

    private static final BigDecimal bigDecimal0 = BigDecimal.ZERO;

    public static void saveAmount(String payload) {
        if (amount.equals(bigDecimal0)) {
            PaymentRequestParse.extractAmountFromRequest(payload);

            amount = new BigDecimal(payload);
        }
    }

    public static BigDecimal getAmount() {
        return amount;
    }
}
