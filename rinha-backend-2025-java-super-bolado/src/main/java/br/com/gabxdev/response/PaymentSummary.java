package br.com.gabxdev.response;

import br.com.gabxdev.model.enums.PaymentProcessorType;

import java.math.BigDecimal;

public final class PaymentSummary {
    PaymentProcessorType type;

    int totalRequests;

    BigDecimal totalAmount;

    private PaymentSummary() {
    }

    public PaymentSummary(PaymentProcessorType type, int totalRequests, BigDecimal totalAmount) {
        this.type = type;
        this.totalRequests = totalRequests;
        this.totalAmount = totalAmount;
    }

    public PaymentProcessorType getType() {
        return type;
    }

    public void setType(PaymentProcessorType type) {
        this.type = type;
    }

    public Integer getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "PaymentSummary{" +
               "type=" + type +
               ", totalRequests=" + totalRequests +
               ", totalAmount=" + totalAmount +
               '}';
    }
}
