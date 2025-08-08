package br.com.gabxdev.repository;

import br.com.gabxdev.mapper.PaymentRequestParse;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

public class Amount {
    private static BigDecimal amount = BigDecimal.ZERO;

    private static final BigDecimal bigDecimal0 = BigDecimal.ZERO;

    public static void saveAmount(byte[] payload) {
        if (amount.equals(bigDecimal0)) {
            amount = new BigDecimal(PaymentRequestParse
                    .extractAmountFromRequest(new String(payload, StandardCharsets.UTF_8)));
        }
    }

    public static BigDecimal getAmount() {
        return amount;
    }
}
