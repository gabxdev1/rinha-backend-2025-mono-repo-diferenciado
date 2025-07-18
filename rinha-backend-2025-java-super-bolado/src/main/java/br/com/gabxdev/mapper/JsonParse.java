package br.com.gabxdev.mapper;

import br.com.gabxdev.model.Payment;
import br.com.gabxdev.response.PaymentSummaryGetResponse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static br.com.gabxdev.mapper.JsonTemplate.PAYMENT_SUMMARY;

public class JsonParse {

    public static Instant parseInstant(String str) {
        try {
            return Instant.parse(str);
        } catch (Exception ex) {
            try {
                return LocalDateTime.parse(str).toInstant(ZoneOffset.UTC);
            } catch (Exception e) {
                return LocalDateTime.of(2000, 1, 1, 0, 0, 0)
                        .toInstant(ZoneOffset.UTC);
            }
        }
    }

    public final static String PAYMENT_POST_REQUEST =
            """
                    {
                        "correlationId": "%s",
                        "amount": %s,
                        "requestedAt" : "%s"
                    }
                    """;

    public static String buildPaymentDTO(Payment payment) {
        return new StringBuilder("{")
                .append("\"correlationId\":\"").append(payment.getCorrelationId()).append("\",")
                .append("\"amount\":").append(payment.getAmount().toPlainString()).append(",")
                .append("\"requestedAt\":\"").append(payment.getRequestedAt().toString()).append("\"")
                .append("}")
                .toString();
    }

    public static String parseToJsonPaymentSummary(PaymentSummaryGetResponse paymentSummary) {
        var totalRequestsDefault = paymentSummary.getDefaultApi().getTotalRequests();
        var totalRequestsFallback = paymentSummary.getFallbackApi().getTotalRequests();


        return buildSummaryJson(
                totalRequestsDefault.toString(),
                paymentSummary.getDefaultApi().getTotalAmount().toPlainString(),
                totalRequestsFallback.toString(),
                paymentSummary.getFallbackApi().getTotalAmount().toPlainString()
        );
    }

    private static String buildSummaryJson(String totalRequestsDefault, String totalAmountDefault,
                                           String totalRequestsFallback,
                                           String amountTotalDefault) {

        return String.format(PAYMENT_SUMMARY,
                totalRequestsDefault,
                totalAmountDefault,
                totalRequestsFallback,
                amountTotalDefault);
    }
}
