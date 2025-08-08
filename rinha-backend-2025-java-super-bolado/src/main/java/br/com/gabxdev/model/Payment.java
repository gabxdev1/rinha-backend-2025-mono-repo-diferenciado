package br.com.gabxdev.model;

import br.com.gabxdev.model.enums.PaymentProcessorType;

public final class Payment {
    private final long requestedAt;

    private byte[] json;

    private PaymentProcessorType type;

    private byte[] payload;

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
}
