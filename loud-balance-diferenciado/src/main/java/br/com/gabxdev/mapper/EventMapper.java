package br.com.gabxdev.mapper;

import br.com.gabxdev.lb.Event;
import br.com.gabxdev.lb.EventType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class EventMapper {
    public static byte[] toPaymentPostRequest(String json) {
        return Event.buildEventDTO(EventType.PAYMENT_POST.ordinal(),
                PaymentRequestParse.buildPayload(json));
    }

    public static byte[] toPurgePaymentsPostRequest() {
        return Event.buildEventDTO(EventType.PURGER.ordinal(), "payload");
    }

    public static byte[] toPaymentSummaryGetRequest(String from, String to) {
        return Event.buildEventDTO(EventType.PAYMENT_SUMMARY.ordinal(),
                from.concat("@").concat(to));
    }
}
