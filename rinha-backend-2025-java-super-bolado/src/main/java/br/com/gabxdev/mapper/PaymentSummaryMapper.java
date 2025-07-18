package br.com.gabxdev.mapper;

import br.com.gabxdev.model.enums.PaymentProcessorType;
import br.com.gabxdev.response.PaymentSummary;
import br.com.gabxdev.response.PaymentSummaryGetResponse;

import java.math.BigDecimal;

public class PaymentSummaryMapper {
    public static PaymentSummaryGetResponse toPaymentSummary(String json) {
        var paymentSummaryDefault = new PaymentSummary(PaymentProcessorType.DEFAULT,
                extractTotalRequests(json, "default"),
                extractTotalAmount(json, "default"));

        var paymentSummaryFallback = new PaymentSummary(PaymentProcessorType.FALLBACK,
                extractTotalRequests(json, "fallback"),
                extractTotalAmount(json, "fallback"));

        return new PaymentSummaryGetResponse(paymentSummaryDefault, paymentSummaryFallback);
    }


    private static BigDecimal extractTotalAmount(String json, String section) {
        return new BigDecimal(extractField(json, section, "totalAmount"));
    }

    private static int extractTotalRequests(String json, String section) {
        return Integer.parseInt(extractField(json, section, "totalRequests"));
    }

    private static String extractField(String json, String section, String field) {
        String sectionKey = "\"" + section + "\":";
        var sectionStart = json.indexOf(sectionKey);
        if (sectionStart == -1) throw new IllegalArgumentException(section + " not found");

        var fieldKeyStart = json.indexOf("\"" + field + "\"", sectionStart);
        if (fieldKeyStart == -1) throw new IllegalArgumentException(field + " not found in " + section);

        var colon = json.indexOf(":", fieldKeyStart);
        var valueStart = colon + 1;

        while (Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }

        var valueEnd = valueStart;

        while (valueEnd < json.length() &&
               (Character.isDigit(json.charAt(valueEnd)) || json.charAt(valueEnd) == '.' || json.charAt(valueEnd) == '-')) {
            valueEnd++;
        }

        return json.substring(valueStart, valueEnd);
    }
}
