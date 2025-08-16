package br.com.gabxdev.model;

import br.com.gabxdev.model.enums.PaymentProcessorType;

import java.math.BigDecimal;
import java.net.http.HttpRequest;

public final class Payment {
    private final long requestedAt;

    private final byte[] payload;

    private byte[] json;

    private PaymentProcessorType type;

    private BigDecimal amount;

    private HttpRequest requestDefault;

    private HttpRequest requestFallback;

    public Payment(long requestedAt, byte[] payload) {
        this.requestedAt = requestedAt;
        this.payload = payload;
    }

    public long getRequestedAt() {
        return requestedAt;
    }

    public byte[] getJson() {
        return json;
    }

    public void setJson(byte[] json) {
        this.json = json;
    }

    public PaymentProcessorType getType() {
        return type;
    }

    public void setType(PaymentProcessorType type) {
        this.type = type;
    }

    public byte[] getPayload() {
        return payload;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public HttpRequest getRequestFallback() {
        return requestFallback;
    }

    public void setRequestFallback(HttpRequest requestFallback) {
        this.requestFallback = requestFallback;
    }

    public HttpRequest getRequestDefault() {
        return requestDefault;
    }

    public void setRequestDefault(HttpRequest requestDefault) {
        this.requestDefault = requestDefault;
    }
}
