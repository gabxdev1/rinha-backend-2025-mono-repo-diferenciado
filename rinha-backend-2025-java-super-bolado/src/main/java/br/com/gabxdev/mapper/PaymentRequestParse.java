package br.com.gabxdev.mapper;

public class PaymentRequestParse {
    private static final String KEY_AMOUNT = "\"amount\":";

    public static String extractAmountFromRequest(String json) {
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
