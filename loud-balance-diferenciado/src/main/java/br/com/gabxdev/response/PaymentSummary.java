package br.com.gabxdev.response;

import java.math.BigDecimal;

public final class PaymentSummary {
    String type;

    int totalRequests;

    BigDecimal totalAmount;

    private PaymentSummary() {
    }

    public PaymentSummary(String type, int totalRequests, BigDecimal totalAmount) {
        this.type = type;
        this.totalRequests = totalRequests;
        this.totalAmount = totalAmount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
