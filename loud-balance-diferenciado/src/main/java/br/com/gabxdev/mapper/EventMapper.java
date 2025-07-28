package br.com.gabxdev.mapper;

import br.com.gabxdev.lb.Event;
import br.com.gabxdev.lb.EventType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class EventMapper {
    public String toPaymentPostRequest(String json) {
        return Event.buildEventDTO(EventType.PAYMENT_POST.ordinal(),
                PaymentRequestParse.buildPayload(json));
    }

    public String toPurgePaymentsPostRequest() {
        return Event.buildEventDTO(EventType.PURGER.ordinal(), "payload");
    }

    public String toPaymentSummaryGetRequest(String from, String to) {
        if (from.isEmpty() || to.isEmpty()) {
            var instant = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
                    .toInstant(ZoneOffset.UTC).toString();

            from = instant;
            to = instant;
        }

        return Event.buildEventDTO(EventType.PAYMENT_SUMMARY.ordinal(),
                from.concat("@").concat(to));
    }
}
