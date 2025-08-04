package br.com.gabxdev.mapper;

import br.com.gabxdev.lb.EventType;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class EventMapper {
    public static byte[] toPaymentPostRequest(byte[] json) {
        var body = Arrays.copyOf(json, json.length + 1);

        body[json.length] = EventType.PAYMENT_POST.getValue();

        return body;
    }

    public static byte[] toPurgePaymentsPostRequest() {
        var bytes = new byte[1];

        bytes[0] = EventType.PURGE.getValue();

        return bytes;
    }

    public static byte[] toPaymentSummaryGetRequest(String from, String to) {
        var payload = from.concat("@").concat(to).getBytes(StandardCharsets.UTF_8);

        var body = Arrays.copyOf(payload, payload.length + 1);

        body[payload.length] = EventType.PAYMENT_SUMMARY.getValue();

        return body;
    }
}
