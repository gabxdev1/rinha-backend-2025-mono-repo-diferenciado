package br.com.gabxdev.mapper;

import br.com.gabxdev.lb.Event;
import br.com.gabxdev.lb.EventType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class EventMapper {
    public String toPaymentPostRequest(String json) {
        return Event.buildEventDTO(" ", EventType.PAYMENT_POST,
                JsonParse.extractUUIDFromRequest(json));
    }

    public String toPurgePaymentsPostRequest() {
        return Event.buildEventDTO(" ", EventType.PURGER, " ");
    }

    public String toPaymentSummaryGetRequest(String from, String to, String uuid) {
        if (from.isEmpty() || to.isEmpty()) {
            var instant = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
                    .toInstant(ZoneOffset.UTC).toString();

            from = instant;
            to = instant;
        }

        return Event.buildEventDTO(uuid,
                EventType.PAYMENT_SUMMARY,
                from.concat("@").concat(to));
    }
}
