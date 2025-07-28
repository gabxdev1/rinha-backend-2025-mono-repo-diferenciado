package br.com.gabxdev.mapper;


import br.com.gabxdev.model.Event;
import br.com.gabxdev.model.enums.EventType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class EventMapper {
    public static String toPaymentPostRequest(String json) {
        return Event.buildEventDTO(EventType.PAYMENT_POST.ordinal(),
                PaymentRequestParse.buildPayload(json));
    }

    public static String toPurgePaymentsPostRequest() {
        return Event.buildEventDTO(EventType.PURGER.ordinal(), "payload");
    }

    public static String toPaymentSummaryGetRequest(String from, String to) {
        return Event.buildEventDTO(
                EventType.PAYMENT_SUMMARY.ordinal(),
                from.concat("@").concat(to));
    }
}
