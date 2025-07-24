package br.com.gabxdev.mapper;

public class PaymentRequestParse {
    private static String KEY_UUID = "\"correlationId\":";
    private static String KEY_AMOUNT = "\"amount\":";


    public static String buildPayload(String json) {
        return new StringBuilder(extractUUIDFromRequest(json))
                .append(" ")
                .append(extractAmountFromRequest(json))
                .toString();
    }

    private static String extractUUIDFromRequest(String json) {
        int idx = json.indexOf(KEY_UUID);

        var start = json.indexOf('"', idx + KEY_UUID.length()) + 1;
        var end = json.indexOf('"', start);
        return json.substring(start, end);
    }

    private static String extractAmountFromRequest(String json) {
        var idx = json.indexOf(KEY_AMOUNT);
        var start = idx + KEY_AMOUNT.length();

        char c;
        do {
            c = json.charAt(start++);
        } while (c == ' ');

        start--;
        var end = start;

        while (true) {
            var ch = json.charAt(end);
            if ((ch >= '0' && ch <= '9') || ch == '.') {
                end++;
            } else {
                break;
            }
        }

        return json.substring(start, end);
    }
}
