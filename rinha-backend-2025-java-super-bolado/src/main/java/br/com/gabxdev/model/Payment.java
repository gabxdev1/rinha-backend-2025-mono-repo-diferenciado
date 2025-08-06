package br.com.gabxdev.model;

import br.com.gabxdev.model.enums.PaymentProcessorType;

import java.math.BigDecimal;
import java.time.Instant;

public final class Payment {
    private final long requestedAt;

    public String json;

    public PaymentProcessorType type;

    public Payment(long requestedAt) {
        this.requestedAt = requestedAt;
    }

    public long getRequestedAt() {
        return requestedAt;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public PaymentProcessorType getType() {
        return type;
    }

    public void setType(PaymentProcessorType type) {
        this.type = type;
    }

}
