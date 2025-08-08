package br.com.gabxdev.mapper;


import br.com.gabxdev.model.Event;
import br.com.gabxdev.model.enums.EventType;

public final class EventMapper {


    public static String toPurgePaymentsPostRequest() {
        return Event.buildEventDTO(EventType.PURGE.ordinal(), "payload");
    }

    public static String toPaymentSummaryGetRequest(String from, String to) {
        return Event.buildEventDTO(
                EventType.PAYMENT_SUMMARY.ordinal(),
                from.concat("@").concat(to));
    }
}
