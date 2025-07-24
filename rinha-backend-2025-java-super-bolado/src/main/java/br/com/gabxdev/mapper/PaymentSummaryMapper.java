package br.com.gabxdev.mapper;

import br.com.gabxdev.model.enums.PaymentProcessorType;
import br.com.gabxdev.response.PaymentSummary;
import br.com.gabxdev.response.PaymentSummaryGetResponse;

import java.math.BigDecimal;

public class PaymentSummaryMapper {
    public static PaymentSummaryGetResponse toPaymentSummary(String json) {
        var summaryJson = json.split("-");

        var paymentSummaryDefault = new PaymentSummary(PaymentProcessorType.DEFAULT,
                parseFieldTotalRequest(summaryJson[0]),
                parseFieldTotalAmount(summaryJson[1]));

        var paymentSummaryFallback = new PaymentSummary(PaymentProcessorType.FALLBACK,
                parseFieldTotalRequest(summaryJson[2]),
                parseFieldTotalAmount(summaryJson[3]));

        return new PaymentSummaryGetResponse(paymentSummaryDefault, paymentSummaryFallback);
    }


    private static BigDecimal parseFieldTotalAmount(String field) {
        return new BigDecimal(field);
    }

    private static int parseFieldTotalRequest(String field) {
        return Integer.parseInt(field);
    }
}
