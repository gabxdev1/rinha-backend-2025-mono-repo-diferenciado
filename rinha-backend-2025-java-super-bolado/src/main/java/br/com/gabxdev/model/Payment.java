package br.com.gabxdev.model;

import br.com.gabxdev.model.enums.PaymentProcessorType;

import java.math.BigDecimal;
import java.time.Instant;

public final class Payment {
    private String correlationId;

    private BigDecimal amount;

    private Instant requestedAt;

    public String json;

    public PaymentProcessorType type;

    public Payment() {
    }

    public Payment(String correlationId, BigDecimal amount, Instant requestedAt) {
        this.correlationId = correlationId;
        this.amount = amount;
        this.requestedAt = requestedAt;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
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
