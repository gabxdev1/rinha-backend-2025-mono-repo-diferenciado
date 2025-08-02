package br.com.gabxdev.mapper;


import br.com.gabxdev.model.Event;
import br.com.gabxdev.model.enums.EventType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class EventMapper {


    public static String toPurgePaymentsPostRequest() {
        return Event.buildEventDTO(EventType.PURGE.ordinal(), "payload");
    }

    public static String toPaymentSummaryGetRequest(String from, String to) {
        if (from.isEmpty() || to.isEmpty()) {
            var instant = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
                    .toInstant(ZoneOffset.UTC).toString();

            from = instant;
            to = instant;
        }

        return Event.buildEventDTO(
                EventType.PAYMENT_SUMMARY.ordinal(),
                from.concat("@").concat(to));
    }
}
