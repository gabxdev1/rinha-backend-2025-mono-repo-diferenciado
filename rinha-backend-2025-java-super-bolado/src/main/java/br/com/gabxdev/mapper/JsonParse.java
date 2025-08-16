package br.com.gabxdev.mapper;

import br.com.gabxdev.response.PaymentSummaryGetResponse;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

public class JsonParse {
    private final static byte[] injectedFieldPrefix = ",\"requestedAt\":\"".getBytes(StandardCharsets.UTF_8);

    private final static byte[] injectedFieldSuffix = "\"}".getBytes(StandardCharsets.UTF_8);

    private static final int START_INDEX_AMOUNT = 62;

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

    public static BigDecimal parseBigDecimal(byte[] payload) {
        int startIndex = 0;
        int endIndex = 0;

        for (int i = START_INDEX_AMOUNT; i < payload.length; i++) {
            if (Character.isDigit(payload[i])) {
                startIndex = i;
                endIndex = i;
                break;
            }
        }

        while (payload[endIndex] != '}') {
            endIndex++;
        }

        var amountStr = new String(Arrays.copyOfRange(payload, startIndex, endIndex), StandardCharsets.UTF_8);

        var amount = new BigDecimal(amountStr);

        return amount;
    }

    public static byte[] buildPaymentDTO(byte[] payload, long requestedAt) {
        byte[] utcBytes = Instant.ofEpochMilli(requestedAt).toString().getBytes(StandardCharsets.UTF_8);

        int originalLengthWithoutClosingBrace = payload.length - 1;

        byte[] finalBytes = new byte[
                originalLengthWithoutClosingBrace +
                injectedFieldPrefix.length +
                utcBytes.length +
                injectedFieldSuffix.length
                ];

        System.arraycopy(payload, 0, finalBytes, 0, originalLengthWithoutClosingBrace);
        System.arraycopy(injectedFieldPrefix, 0, finalBytes, originalLengthWithoutClosingBrace, injectedFieldPrefix.length);
        System.arraycopy(utcBytes, 0, finalBytes, originalLengthWithoutClosingBrace + injectedFieldPrefix.length, utcBytes.length);
        System.arraycopy(injectedFieldSuffix, 0, finalBytes, finalBytes.length - injectedFieldSuffix.length, injectedFieldSuffix.length);

        return finalBytes;
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

        return "{" +
               "\"default\": {" +
               "\"totalRequests\":" + totalRequestsDefault + "," +
               "\"totalAmount\":" + totalAmountDefault +
               "}," +
               "\"fallback\": {" +
               "\"totalRequests\":" + totalRequestsFallback + "," +
               "\"totalAmount\":" + amountTotalDefault +
               "}" +
               "}";
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

        String sb = totalRequestsDefault +
                    "-" +
                    totalAmountDefault +
                    "-" +
                    totalRequestsFallback +
                    "-" +
                    amountTotalDefault;

        return sb;
    }
}
