package br.com.gabxdev.response;

public final class PaymentSummaryGetResponse {
    private PaymentSummary defaultApi;

    private PaymentSummary fallbackApi;

    public PaymentSummaryGetResponse() {
    }

    public PaymentSummaryGetResponse(PaymentSummary defaultApi, PaymentSummary fallbackApi) {
        this.defaultApi = defaultApi;
        this.fallbackApi = fallbackApi;
    }

    public PaymentSummary getDefaultApi() {
        return defaultApi;
    }

    public void setDefaultApi(PaymentSummary defaultApi) {
        this.defaultApi = defaultApi;
    }

    public PaymentSummary getFallbackApi() {
        return fallbackApi;
    }

    public void setFallbackApi(PaymentSummary fallbackApi) {
        this.fallbackApi = fallbackApi;
    }

    @Override
    public String toString() {
        return "PaymentSummaryGetResponse{" +
               "defaultApi=" + defaultApi +
               ", fallbackApi=" + fallbackApi +
               '}';
    }
}
