package br.com.gabxdev.mapper;

import br.com.gabxdev.model.Payment;
import br.com.gabxdev.repository.Amount;
import br.com.gabxdev.response.PaymentSummaryGetResponse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class JsonParse {

    public static long parseInstant(String str) {
        try {
            return Instant.parse(str).toEpochMilli();
        } catch (Exception ex) {
            try {
                return LocalDateTime.parse(str).toInstant(ZoneOffset.UTC).toEpochMilli();
            } catch (Exception e) {
                return LocalDateTime.of(2000, 1, 1, 0, 0, 0)
                        .toInstant(ZoneOffset.UTC).toEpochMilli();
            }
        }
    }

    public static String buildPaymentDTO(String uuid, Payment payment) {
        return new StringBuilder("{")
                .append("\"correlationId\":\"").append(uuid).append("\",")
                .append("\"amount\":").append(Amount.getAmount().toPlainString()).append(",")
                .append("\"requestedAt\":\"").append(Instant.ofEpochMilli(payment.getRequestedAt()).toString()).append("\"")
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

        return new StringBuilder("{")
                .append("\"default\": {")
                .append("\"totalRequests\":").append(totalRequestsDefault).append(",")
                .append("\"totalAmount\":").append(totalAmountDefault)
                .append("},")
                .append("\"fallback\": {")
                .append("\"totalRequests\":").append(totalRequestsFallback).append(",")
                .append("\"totalAmount\":").append(amountTotalDefault)
                .append("}")
                .append("}")
                .toString();
    }

    public static String parseToJsonPaymentSummaryInternal(PaymentSummaryGetResponse paymentSummary) {
        var totalRequestsDefault = paymentSummary.getDefaultApi().getTotalRequests();
        var totalRequestsFallback = paymentSummary.getFallbackApi().getTotalRequests();


        return buildSummaryJsonInternal(
                totalRequestsDefault.toString(),
                paymentSummary.getDefaultApi().getTotalAmount().toPlainString(),
                totalRequestsFallback.toString(),
                paymentSummary.getFallbackApi().getTotalAmount().toPlainString()
        );
    }

    private static String buildSummaryJsonInternal(String totalRequestsDefault, String totalAmountDefault,
                                                   String totalRequestsFallback,
                                                   String amountTotalDefault) {

        var sb = new StringBuilder(100);

        return sb.append(totalRequestsDefault)
                .append("-")
                .append(totalAmountDefault)
                .append("-")
                .append(totalRequestsFallback)
                .append("-")
                .append(amountTotalDefault)
                .toString();
    }
}
