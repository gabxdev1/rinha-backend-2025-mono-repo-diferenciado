package br.com.gabxdev.client;

import br.com.gabxdev.config.HttpClientConfig;
import br.com.gabxdev.model.Payment;
import br.com.gabxdev.model.enums.PaymentProcessorType;
import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class PaymentProcessorClient {

    private final static PaymentProcessorClient INSTANCE = new PaymentProcessorClient();

    private final HttpClient httpClient = HttpClientConfig.httpClient();

    public final int retryApiDefault;

    private PaymentProcessorClient() {
        var properties = ApplicationProperties.getInstance();

        var retryS = properties.getProperty(PropertiesKey.RETRY_API_DEFAULT);

        retryApiDefault = Integer.parseInt(retryS);
    }

    public static PaymentProcessorClient getInstance() {
        return INSTANCE;
    }

    public boolean sendPayment(Payment request) {
        if (sendPaymentDefaultWithRetry(request.getRequestDefault())) {
            request.setType(PaymentProcessorType.DEFAULT);

            return true;
        }

        if (callApiFallBack(request.getRequestFallback())) {
            request.setType(PaymentProcessorType.FALLBACK);

            return true;
        }

        return false;
    }

    private boolean sendPaymentDefaultWithRetry(HttpRequest request) {
        for (int i = 1; i <= retryApiDefault; i++) {
            if (sendRequest(request)) {
                return true;
            }
        }

        return false;
    }

    private boolean callApiFallBack(HttpRequest request) {
        return sendRequest(request);
    }

    private boolean sendRequest(HttpRequest request) {
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            return response.statusCode() == 200 || response.statusCode() == 422;
        } catch (Exception e) {
            return false;
        }
    }
}
