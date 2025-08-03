package br.com.gabxdev.repository;

import java.math.BigDecimal;

public class Amount {
    private static BigDecimal amount = BigDecimal.ZERO;

    private static final BigDecimal bigDecimal0 = BigDecimal.ZERO;

    public static void saveAmount(String amountS) {
        if (amount.equals(bigDecimal0)) {
            amount = new BigDecimal(amountS);
        }
    }

    public static BigDecimal getAmount() {
        return amount;
    }
}
