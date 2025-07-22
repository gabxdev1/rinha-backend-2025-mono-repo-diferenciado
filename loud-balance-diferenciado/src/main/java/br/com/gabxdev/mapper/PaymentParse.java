package br.com.gabxdev.mapper;

import java.time.Instant;

public class PaymentParse {
    private final static String key = "\"correlationId\":";

    public static String extractUUIDFromRequest(String json) {
        var start = json.indexOf('"', json.indexOf(key) + key.length()) + 1;
        var end = json.indexOf('"', start);

        return json.substring(start, end);
    }

}
